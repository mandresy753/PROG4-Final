
create index if not exists idx_enrollments_student_level_start
    on enrollments (student_id, level, start_date);
create index if not exists idx_enrollments_group_year
    on enrollments (group_id, academic_year_id);

create index if not exists idx_groups_track on groups (track);

create index if not exists idx_course_offerings_group_year
    on course_offerings (group_id, academic_year_id);

create index if not exists idx_exams_course_offering on exams (course_offering_id);

create index if not exists idx_grades_exam_student_entry_date
    on grades (exam_id, student_id, entry_date desc);
create index if not exists idx_grades_student on grades (student_id);
