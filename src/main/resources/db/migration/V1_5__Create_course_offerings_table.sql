-- Un CourseOffering représente un cours donné une année donnée. Il peut être
-- partagé par plusieurs groupes (tronc commun S1-S3, ou sous-ensemble de
-- groupes à partir de S4 quand EL/TN se séparent) : voir course_offering_groups.
create table if not exists course_offerings
(
    id                uuid
        constraint course_offerings_pk primary key,
    course_id         uuid not null
        constraint course_offerings_course_fk references courses,
    academic_year_id  uuid not null
        constraint course_offerings_academic_year_fk references academic_years
);
