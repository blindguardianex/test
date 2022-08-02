-- Таблица профилей
create table if not exists "user"
(
    "id"        bigserial       PRIMARY KEY,
    "name"      varchar(500)    not null,
    "password"  varchar(500)    not null,
    birth_date  date            not null
);
comment on table "user" is 'Таблица пользователей';
comment on column "user".id is 'ИД пользователя';
comment on column "user".name is 'Логин пользователя';
comment on column "user".password is 'Пароль пользователя';
comment on column "user".birth_date is 'Дата рождения пользователя';

-- Таблица аккаунтов
create table if not exists account(
    "id"		bigserial	    PRIMARY KEY,
    user_id 	bigserial       not null    unique  constraint fk_account_user references "user" on update cascade on delete cascade,
    balance		decimal         not null
);
comment on table account is 'Аккаунты пользователей';
comment on column account.id is 'ИД аккаунта';
comment on column account.user_id is 'ИД связанного пользователя';
comment on column account.balance is 'Денежный баланс аккаунта';

-- Таблица емэйлов
create table if not exists email_data(
    "id"		bigserial	    PRIMARY KEY,
    user_id 	bigserial       not null    constraint fk_email_user references "user" on update cascade on delete cascade,
    email		varchar(200)    not null    unique
);
comment on table email_data is 'Адреса электронных почт пользователей';
comment on column email_data.id is 'ИД почты';
comment on column email_data.user_id is 'ИД связанного пользователя';
comment on column email_data.email is 'Электронная почта';

-- Таблица телефонов
create table if not exists phone_data(
    "id"		bigserial	    PRIMARY KEY,
    user_id 	bigserial       not null    constraint fk_phone_user references "user" on update cascade on delete cascade,
    phone		varchar(200)    not null    unique
);
comment on table phone_data is 'Телефонные номера пользователей';
comment on column phone_data.id is 'ИД телефонного номера';
comment on column phone_data.user_id is 'ИД связанного пользователя';
comment on column phone_data.phone is 'Телефонный номер';