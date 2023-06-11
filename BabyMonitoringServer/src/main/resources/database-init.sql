Create table if not exists feeding_records(
    id serial primary key,
    milk float,
    sessionstarttime timestamptz,
    sessionendtime timestamptz
);

insert into feeding_records(milk, sessionstarttime, sessionendtime) values(350, '2023-06-10 12:06:54.00', '2023-06-10 12:15:00') on conflict do nothing;
insert into feeding_records(milk, sessionstarttime, sessionendtime) values(500, '2023-06-10 13:10:00', '2023-06-10 13:52:00') on conflict do nothing;
insert into feeding_records(milk, sessionstarttime, sessionendtime) values(300, now(), now()) on conflict do nothing;


