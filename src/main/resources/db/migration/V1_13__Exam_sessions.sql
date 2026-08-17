create table if not exists exam_sessions
(
    id         uuid
        constraint exam_sessions_pk primary key,
    exam_id    uuid      not null
        constraint exam_sessions_exam_fk references exams,
    exam_date  timestamp not null,
    teacher_id uuid
        constraint exam_sessions_teacher_fk references users
);

create table if not exists exam_session_groups
(
    exam_session_id uuid not null
        constraint esg_session_fk references exam_sessions,
    group_id        uuid not null
        constraint esg_group_fk references groups,
    constraint esg_pk primary key (exam_session_id, group_id)
);

insert into exam_sessions (id, exam_id, exam_date)
select e.id, e.id, e.exam_date
from exams e;

insert into exam_session_groups (exam_session_id, group_id)
select es.id, cog.group_id
from exam_sessions es
         join exams e on e.id = es.exam_id
         join course_offering_groups cog on cog.course_offering_id = e.course_offering_id;

alter table exams
    drop column exam_date;

alter table grades
    add column exam_session_id uuid
        constraint grades_exam_session_fk references exam_sessions;

update grades
set exam_session_id = exam_id;

alter table grades
    alter column exam_session_id set not null;

alter table grades
    drop constraint grades_exam_fk;

alter table grades
    drop column exam_id;
