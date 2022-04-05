insert into public.eventtypes (name)
values ('Деловые'),
       ('Учебные')
        ,
       ('Развлекательные')
        ,
       ('Спортивные')
        ,
       ('Другое')
on conflict do nothing;


