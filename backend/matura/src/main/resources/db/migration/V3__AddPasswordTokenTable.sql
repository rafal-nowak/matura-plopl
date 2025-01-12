create sequence reset_password_token_id_seq start with 1 increment by 1;

create table reset_password_token_entity
(
    id         integer                     not null,
    expires_at timestamp(6) with time zone not null,
    email      varchar(255)                not null,
    token      varchar(255)                not null,
    primary key (id)
);