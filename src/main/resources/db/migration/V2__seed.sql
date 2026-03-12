create extension if not exists pgcrypto;

insert into products (id, code, name, category, is_active, created_at)
values ('7f4a3b2e-1d9c-4f7a-8b3e-2c1d9e7f4a11'::uuid, 'P001', 'Mobile Topup', 'TELCO', true, current_timestamp),
       ('1c9d7e4a-3b2f-48d1-9a7c-5e2b4f8d1a22'::uuid, 'P002', 'Utility Payment', 'BILLING', true, current_timestamp),
       ('9a2f4d7c-6b1e-45f8-8c2d-7e4a1b3c5d33'::uuid, 'P003', 'Game Voucher', 'DIGITAL', true, current_timestamp),
       ('2d7a1c4e-8f3b-4d9a-b2c1-6e7f4a3b2c44'::uuid, 'P004', 'Internet Package', 'TELCO', true, current_timestamp),
       ('5e1b3d7a-4c9f-46a2-8d1e-3f7a2b4c5d55'::uuid, 'P005', 'Cinema Ticket', 'ENTERTAINMENT', true,
        current_timestamp),
       ('8c4d2a7f-1e3b-49d5-a6c2-4b7e1d3f6a66'::uuid, 'P006', 'Food Delivery', 'FOOD', true, current_timestamp),
       ('3f7a2c1d-5e4b-47a9-8d3c-1b6e4f2a7d77'::uuid, 'P007', 'Transport Card', 'TRANSPORT', true, current_timestamp),
       ('6b2d4f8a-7c1e-4a3d-9f2b-5e1c7a4d8f88'::uuid, 'P008', 'Marketplace Order', 'ECOM', true, current_timestamp);

insert into merchants (id, code, name, mcc, country, city, is_active, created_at)
values ('a14f3c7d-92b1-4e5a-8c3d-1f2a7b4c9d01'::uuid, 'M001', 'Shop A', '5411', 'KZ', 'Almaty', true,
        current_timestamp),
       ('b25e4d8a-13c7-4f6b-9d2a-2e3b8c5d1f02'::uuid, 'M002', 'Shop B', '5732', 'KZ', 'Astana', true,
        current_timestamp),
       ('c36a5e9b-24d8-4a7c-8e3b-3f4c9d6a2b03'::uuid, 'M003', 'Shop C', '5812', 'KZ', 'Shymkent', true,
        current_timestamp),
       ('d47b6f1c-35e9-4b8d-9f4c-4a5d1e7b3c04'::uuid, 'M004', 'Shop D', '5999', 'KZ', 'Karaganda', true,
        current_timestamp),
       ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05'::uuid, 'M005', 'Shop E', '4900', 'KZ', 'Aktobe', true,
        current_timestamp);

insert into agents (id, code, name, type, country, is_active, created_at)
values ('f69d8b3e-57a2-4d1f-9b6e-6c7f3a9d5e11'::uuid, 'A01', 'Mobile App', 'APP', 'KZ', true, current_timestamp),
       ('0a7e9c4f-68b3-4e2a-8c7f-7d8a4b1e6f22'::uuid, 'A02', 'POS Partner', 'POS', 'KZ', true, current_timestamp),
       ('1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid, 'A03', 'Web Checkout', 'WEB', 'KZ', true, current_timestamp);

insert into users (external_id, email, full_name, phone, country, created_at)
select 'U-' || lpad(gs::text, 4, '0'),
       'user' || gs || '@mail.com',
       'User ' || gs,
       '+7701' || lpad(gs::text, 7, '0'),
       'KZ',
       current_timestamp - ((20 - gs) || ' days')::interval
from generate_series(1, 20) gs;

insert into merchant_products (merchant_id, product_id)
select m.id, p.id
from merchants m
         cross join products p;

insert into agent_merchants (agent_id, merchant_id)
select a.id, m.id
from agents a
         cross join merchants m;

insert into fee (merchant_id,
                 agent_id,
                 product_id,
                 currency,
                 calc_type,
                 rate,
                 fixed_amount,
                 min_amount,
                 max_amount,
                 is_active,
                 valid_from,
                 valid_to)
select m.id,
       a.id,
       p.id,
       'KZT',
       case
           when mod(row_number() over (order by m.code, a.code, p.code), 3) = 1 then 'PERCENT'
           when mod(row_number() over (order by m.code, a.code, p.code), 3) = 2 then 'FIXED'
           else 'PERCENT_WITH_CAP'
           end,
       case
           when mod(row_number() over (order by m.code, a.code, p.code), 3) in (1, 0)
               then round((0.010 + random() * 0.020)::numeric, 4)
           else null
           end,
       case
           when mod(row_number() over (order by m.code, a.code, p.code), 3) = 2
               then round((50 + random() * 200)::numeric, 2)
           else null
           end,
       round((10 + random() * 40)::numeric, 2),
       round((300 + random() * 700)::numeric, 2),
       true,
       current_timestamp - ((10 + floor(random() * 30))::int || ' days')::interval,
       current_timestamp + ((180 + floor(random() * 180))::int || ' days')::interval
from merchants m
         cross join agents a
         cross join products p
where not (
    (m.code = 'M005' and a.code = 'A03' and p.code in ('P001', 'P002', 'P003', 'P004', 'P005', 'P006', 'P007', 'P008'))
        or
    (m.code = 'M004' and a.code = 'A03' and p.code in ('P007', 'P008'))
    );

with fee_combos as (select f.merchant_id,
                           f.agent_id,
                           f.product_id,
                           row_number() over (order by f.merchant_id, f.agent_id, f.product_id) as rn
                    from fee f),
     users_r as (select id,
                        row_number() over (order by email) as rn
                 from users),
     fee_count as (select count(*) as cnt
                   from fee_combos),
     user_count as (select count(*) as cnt
                    from users_r),
     src as (select gs          as n,
                    (gs <= 133) as is_success
             from generate_series(1, 190) gs)
insert
into transactions (tx_id,
                   created_at,
                   finished_at,
                   buyer_user_id,
                   merchant_id,
                   agent_id,
                   product_id,
                   amount,
                   currency,
                   status,
                   result_code,
                   success,
                   rrn,
                   stan,
                   payment_method)
select 'TX-' || to_char(current_date, 'YYYYMMDD') || '-' || lpad(src.n::text, 6, '0'),
       current_timestamp - ((1 + floor(random() * 60))::int || ' days')::interval,
       current_timestamp - ((1 + floor(random() * 60))::int || ' days')::interval
           + ((5 + floor(random() * 120))::int || ' seconds')::interval,
       u.id,
       fc.merchant_id,
       fc.agent_id,
       fc.product_id,
       round((500 + random() * 49500)::numeric, 2),
       'KZT',
       case when src.is_success then 'SUCCESS' else 'FAILED' end,
       case when src.is_success then 'APPROVED' else 'DECLINED' end,
       src.is_success,
       lpad(floor(random() * 999999999999)::bigint::text, 12, '0'),
       lpad(floor(random() * 999999)::int::text, 6, '0'),
       case
           when mod(src.n, 3) = 0 then 'CARD'
           when mod(src.n, 3) = 1 then 'QR'
           else 'APPLE_PAY'
           end
from src
         cross join fee_count
         cross join user_count
         join fee_combos fc on fc.rn = ((src.n - 1) % fee_count.cnt) + 1
         join users_r u on u.rn = ((src.n - 1) % user_count.cnt) + 1;

with missing_combos as (select t.merchant_id,
                               t.agent_id,
                               t.product_id,
                               row_number() over () as rn
                        from (values ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '7f4a3b2e-1d9c-4f7a-8b3e-2c1d9e7f4a11'::uuid),
                                     ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '1c9d7e4a-3b2f-48d1-9a7c-5e2b4f8d1a22'::uuid),
                                     ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '9a2f4d7c-6b1e-45f8-8c2d-7e4a1b3c5d33'::uuid),
                                     ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '2d7a1c4e-8f3b-4d9a-b2c1-6e7f4a3b2c44'::uuid),
                                     ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '5e1b3d7a-4c9f-46a2-8d1e-3f7a2b4c5d55'::uuid),
                                     ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '8c4d2a7f-1e3b-49d5-a6c2-4b7e1d3f6a66'::uuid),
                                     ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '3f7a2c1d-5e4b-47a9-8d3c-1b6e4f2a7d77'::uuid),
                                     ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '6b2d4f8a-7c1e-4a3d-9f2b-5e1c7a4d8f88'::uuid),
                                     ('d47b6f1c-35e9-4b8d-9f4c-4a5d1e7b3c04'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '3f7a2c1d-5e4b-47a9-8d3c-1b6e4f2a7d77'::uuid),
                                     ('d47b6f1c-35e9-4b8d-9f4c-4a5d1e7b3c04'::uuid,
                                      '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33'::uuid,
                                      '6b2d4f8a-7c1e-4a3d-9f2b-5e1c7a4d8f88'::uuid)) as t(merchant_id, agent_id, product_id)),
     users_r as (select id,
                        row_number() over (order by email) as rn
                 from users),
     src as (select gs        as n,
                    (gs <= 7) as is_success
             from generate_series(1, 10) gs)
insert
into transactions (tx_id,
                   created_at,
                   finished_at,
                   buyer_user_id,
                   merchant_id,
                   agent_id,
                   product_id,
                   amount,
                   currency,
                   status,
                   result_code,
                   success,
                   rrn,
                   stan,
                   payment_method)
select 'TX-NF-' || to_char(current_date, 'YYYYMMDD') || '-' || lpad(src.n::text, 4, '0'),
       current_timestamp - ((1 + floor(random() * 30))::int || ' days')::interval,
       current_timestamp - ((1 + floor(random() * 30))::int || ' days')::interval
           + ((5 + floor(random() * 120))::int || ' seconds')::interval,
       u.id,
       mc.merchant_id,
       mc.agent_id,
       mc.product_id,
       round((500 + random() * 49500)::numeric, 2),
       'KZT',
       case when src.is_success then 'SUCCESS' else 'FAILED' end,
       case when src.is_success then 'APPROVED' else 'DECLINED' end,
       src.is_success,
       lpad(floor(random() * 999999999999)::bigint::text, 12, '0'),
       lpad(floor(random() * 999999)::int::text, 6, '0'),
       'CARD'
from src
         join missing_combos mc on mc.rn = src.n
         join users_r u on u.rn = src.n;