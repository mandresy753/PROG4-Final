package com.example.demo.repository;

import com.example.demo.entity.JUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Computes the ranked graduate list entirely in SQL instead of loading every student and every
 * grade into the JVM and averaging in Java. The previous approach issued one query per
 * course-offering per student (classic N+1) and did the weighting/ranking in a Java stream; on a
 * promotion with a large student body that pattern is what causes slow requests / gateway timeouts.
 * This single set-based query lets Postgres do the aggregation, and only ever returns the
 * (typically small) list of students who actually graduated.
 *
 * <p>Indexes supporting this query are created in V1_11__Add_performance_indexes.sql.
 */
public interface GraduationQueryRepository extends Repository<JUser, UUID> {

  @Query(
      value =
          """
WITH student_promotion AS (
    -- The promotion is the academic year in which the student was first enrolled at L1,
    -- independent of any group change afterwards.
    SELECT DISTINCT ON (e.student_id)
        e.student_id, ay.label AS promotion
    FROM enrollments e
    JOIN academic_years ay ON ay.id = e.academic_year_id
    WHERE e.level = 'L1'
    ORDER BY e.student_id, e.start_date ASC
),
student_track AS (
    -- A student must have a single, consistent track across all their enrollments.
    SELECT e.student_id, MIN(g.track) AS track, COUNT(DISTINCT g.track) AS track_count
    FROM enrollments e
    JOIN groups g ON g.id = e.group_id
    GROUP BY e.student_id
),
eligible_students AS (
    SELECT sp.student_id
    FROM student_promotion sp
    JOIN student_track st ON st.student_id = sp.student_id
    WHERE sp.promotion = :promotion
      AND st.track_count = 1
      AND st.track = :track
),
student_offerings AS (
    -- Every course-offering a student is responsible for, across every group/year
    -- they were ever enrolled in (this is how a mid-year group change is handled:
    -- both the old and the new group's offerings for that year are included).
    SELECT DISTINCT e.student_id, co.id AS course_offering_id, co.course_id
    FROM enrollments e
    JOIN course_offerings co
      ON co.group_id = e.group_id AND co.academic_year_id = e.academic_year_id
    WHERE e.student_id IN (SELECT student_id FROM eligible_students)
),
exam_totals AS (
    SELECT course_offering_id, SUM(coefficient) AS total_coefficient
    FROM exams
    GROUP BY course_offering_id
),
latest_grades AS (
    -- Only the most recent grade per exam/student counts; older rows are kept for
    -- history (reclamations, corrections) but must not be double-counted here.
    SELECT DISTINCT ON (g.exam_id, g.student_id)
        g.exam_id, g.student_id, g.value
    FROM grades g
    WHERE g.student_id IN (SELECT student_id FROM eligible_students)
    ORDER BY g.exam_id, g.student_id, g.entry_date DESC
),
course_stats AS (
    SELECT
        so.student_id,
        so.course_offering_id,
        so.course_id,
        et.total_coefficient,
        COALESCE(SUM(ex.coefficient) FILTER (WHERE lg.value IS NOT NULL), 0) AS graded_coefficient,
        COALESCE(SUM(ex.coefficient * lg.value) FILTER (WHERE lg.value IS NOT NULL), 0) AS weighted_sum
    FROM student_offerings so
    JOIN exam_totals et ON et.course_offering_id = so.course_offering_id
    JOIN exams ex ON ex.course_offering_id = so.course_offering_id
    LEFT JOIN latest_grades lg
      ON lg.exam_id = ex.id AND lg.student_id = so.student_id
    GROUP BY so.student_id, so.course_offering_id, so.course_id, et.total_coefficient
),
course_results AS (
    SELECT
        cs.student_id,
        c.credit_count,
        CASE WHEN cs.graded_coefficient > 0
             THEN cs.weighted_sum / cs.graded_coefficient
             ELSE NULL END AS course_average,
        (cs.total_coefficient = 1 AND cs.graded_coefficient = cs.total_coefficient) AS course_complete
    FROM course_stats cs
    JOIN courses c ON c.id = cs.course_id
),
student_summary AS (
    SELECT
        student_id,
        SUM(credit_count) AS total_credits,
        SUM(course_average * credit_count) FILTER (WHERE course_average IS NOT NULL) AS weighted_credit_sum,
        SUM(credit_count) FILTER (WHERE course_average IS NOT NULL) AS graded_credits,
        BOOL_AND(course_complete) AS all_complete,
        BOOL_AND(course_average IS NOT NULL AND course_average >= 10) AS all_validated
    FROM course_results
    GROUP BY student_id
)
SELECT
    u.id AS id,
    u.reference AS reference,
    u.last_name AS lastName,
    u.first_name AS firstName,
    u.email AS email,
    ROUND(ss.weighted_credit_sum / NULLIF(ss.graded_credits, 0), 2) AS overallAverage,
    ss.total_credits AS totalCredits,
    RANK() OVER (
        ORDER BY (ss.weighted_credit_sum / NULLIF(ss.graded_credits, 0)) DESC
    ) AS rank
FROM student_summary ss
JOIN users u ON u.id = ss.student_id
WHERE ss.all_complete = TRUE
  AND ss.all_validated = TRUE
  AND ss.total_credits >= :expectedCredits
ORDER BY rank ASC, u.last_name ASC
""",
      nativeQuery = true)
  List<GraduateRankingRow> findRankedGraduates(
      @Param("track") String track,
      @Param("promotion") String promotion,
      @Param("expectedCredits") int expectedCredits);
}
