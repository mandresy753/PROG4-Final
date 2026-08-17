-- Un Exam porte le coefficient d'une évaluation pour un cours (ex: "Examen 1",
-- 20%). La date/heure/prof/groupes concrets sont sur exam_sessions, car un
-- même Exam peut se dérouler à des moments différents selon le groupe.
create table if not exists exams
(
    id                  uuid
        constraint exams_pk primary key,
    course_offering_id  uuid          not null
        constraint exams_course_offering_fk references course_offerings,
    coefficient         numeric(4, 3) not null
);
