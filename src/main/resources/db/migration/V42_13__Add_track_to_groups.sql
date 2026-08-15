alter table groups
    add column track varchar not null default 'EL';

alter table groups
    add constraint groups_track_check check (track in ('EL', 'TN'));

alter table groups
    alter column track drop default;
