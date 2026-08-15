create table if not exists groups
(
    id uuid constraint groups_pk primary key,
    reference varchar not null constraint groups_reference_uk unique,
    track varchar not null constraint groups_track_check check (track in ('EL', 'TN'))
);
