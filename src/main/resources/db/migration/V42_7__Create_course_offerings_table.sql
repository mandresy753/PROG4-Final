create table if not exists course_offerings
(
    id uuid constraint course_offerings_pk primary key,
    course_id uuid not null constraint course_offerings_course_fk references courses,
    academic_year_id uuid not null constraint course_offerings_academic_year_fk references academic_years,
    group_id  uuid not null constraint course_offerings_group_fk references groups,
    constraint course_offerings_course_year_group_uk unique (course_id, academic_year_id, group_id)
);
