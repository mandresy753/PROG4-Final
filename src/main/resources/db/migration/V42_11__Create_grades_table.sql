create table if not exists grades
(
    id uuid constraint grades_pk primary key,
    exam_id uuid not null constraint grades_exam_fk references exams,
    student_id uuid not null constraint grades_student_fk references users,
    value numeric(4, 2) not null,
    entry_date timestamp     not null,
    entered_by_id uuid not null constraint grades_entered_by_fk references users,
    reason varchar
);
