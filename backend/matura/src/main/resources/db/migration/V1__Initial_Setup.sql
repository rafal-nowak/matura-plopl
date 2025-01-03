create sequence result_id_seq start with 1 increment by 1;
create sequence submission_id_seq start with 1 increment by 1;
create sequence task_id_seq start with 1 increment by 1;
create sequence template_id_seq start with 1 increment by 1;
create sequence user_id_seq start with 1 increment by 1;
create table result_entity
(
    id             integer                     not null,
    score          integer                     not null,
    submission_id  integer                     not null,
    subtask_number integer                     not null,
    created_at     timestamp(6) with time zone not null,
    description    TEXT                        not null,
    primary key (id)
);
create table submission_entity
(
    id           integer                     not null,
    submitted_by integer                     not null,
    task_id      integer                     not null,
    verification smallint                    not null check (verification between 0 and 1),
    submitted_at timestamp(6) with time zone not null,
    primary key (id)
);
create table task_entity
(
    created_by    integer                     not null,
    id            integer                     not null,
    state         smallint                    not null check (state between 0 and 2),
    template_id   integer                     not null,
    user_id       integer                     not null,
    created_at    timestamp(6) with time zone not null,
    workspace_url varchar(255)                not null,
    primary key (id)
);
create table template_entity
(
    created_by         integer,
    id                 integer                     not null,
    number_of_subtasks integer                     not null,
    task_language      smallint                    not null check (task_language between 0 and 3),
    created_at         timestamp(6) with time zone not null,
    source             varchar(255)                not null,
    source_url         varchar(255)                not null,
    statement          TEXT                        not null,
    primary key (id)
);
create table user_entity
(
    created_by integer,
    id         integer                     not null,
    role       smallint                    not null check (role between 0 and 2),
    created_at timestamp(6) with time zone not null,
    email      varchar(255)                not null,
    password   varchar(255)                not null,
    username   varchar(255)                not null,
    primary key (id),
    constraint user_email_unique unique (email)
);