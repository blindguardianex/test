insert into "user"(id, name, password, birth_date)
    values
        (nextval('user_id_seq'), 'coward', '{noop}password', now()),
        (nextval('user_id_seq'), 'moron', '{noop}password', now()),
        (nextval('user_id_seq'), 'veteran', '{noop}password', now());

insert into account(id, user_id, balance)
    values
        (nextval('account_id_seq'), (select id from "user" u where u.name='coward'), 1000000),
        (nextval('account_id_seq'), (select id from "user" u where u.name='moron'), 500000),
        (nextval('account_id_seq'), (select id from "user" u where u.name='veteran'), 3300030);

insert into email_data(id, user_id, email)
    values
        (nextval('email_data_id_seq'), (select id from "user" u where u.name='coward'), 'coward@mail.ru'),
        (nextval('email_data_id_seq'), (select id from "user" u where u.name='moron'), 'moron@mail.ru'),
        (nextval('email_data_id_seq'), (select id from "user" u where u.name='veteran'), 'veteran@mail.ru');

insert into phone_data(id, user_id, phone)
    values
        (nextval('phone_data_id_seq'), (select id from "user" u where u.name='coward'), '+1(111)111-1111'),
        (nextval('phone_data_id_seq'), (select id from "user" u where u.name='moron'), '+2(222)222-2222'),
        (nextval('phone_data_id_seq'), (select id from "user" u where u.name='veteran'), '+3(333)333-3333');
