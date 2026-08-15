alter table courses
    add column semester varchar not null default 'S1';

alter table courses
    add constraint courses_semester_check check (semester in ('S1', 'S2', 'S3', 'S4', 'S5', 'S6'));

alter table courses
    alter column semester drop default;
