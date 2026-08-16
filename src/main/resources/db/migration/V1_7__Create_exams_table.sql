create table if not exists exams
(
    id                  uuid
        constraint exams_pk primary key,
    course_offering_id  uuid          not null
        constraint exams_course_offering_fk references course_offerings,
    exam_date           date          not null,
    coefficient         numeric(4, 3) not null
);
