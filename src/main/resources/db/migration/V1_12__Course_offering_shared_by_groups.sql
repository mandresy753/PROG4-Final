create table if not exists course_offering_groups
(
    course_offering_id uuid not null
        constraint cog_offering_fk references course_offerings,
    group_id            uuid not null
        constraint cog_group_fk references groups,
    constraint cog_pk primary key (course_offering_id, group_id)
);

insert into course_offering_groups (course_offering_id, group_id)
select id, group_id
from course_offerings;

alter table course_offerings drop constraint course_offerings_course_year_group_uk;
alter table course_offerings drop constraint course_offerings_group_fk;
alter table course_offerings drop column group_id;
