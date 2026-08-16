-- Supports GraduationQueryRepository.findRankedGraduates, which computes averages, completion
-- and ranking for a whole promotion in a single SQL query instead of one Java pass per student.

-- Finding a student's earliest L1 enrollment (their promotion) and their groups/years.
create index if not exists idx_enrollments_student_level_start
    on enrollments (student_id, level, start_date);
create index if not exists idx_enrollments_group_year
    on enrollments (group_id, academic_year_id);

-- Resolving a group's track for the single-track consistency check.
create index if not exists idx_groups_track on groups (track);

-- Resolving which course_offerings a group/year owes its students.
create index if not exists idx_course_offerings_group_year
    on course_offerings (group_id, academic_year_id);

-- Summing exam coefficients per course_offering.
create index if not exists idx_exams_course_offering on exams (course_offering_id);

-- Picking the latest grade per exam/student without scanning the whole table.
create index if not exists idx_grades_exam_student_entry_date
    on grades (exam_id, student_id, entry_date desc);
create index if not exists idx_grades_student on grades (student_id);
