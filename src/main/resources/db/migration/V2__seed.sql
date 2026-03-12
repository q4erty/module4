INSERT INTO products (id, code, name, category, is_active, created_at)
VALUES ('7f4a3b2e-1d9c-4f7a-8b3e-2c1d9e7f4a11', 'P001', 'Mobile Topup', 'TELCO', true, NOW(6)),
       ('1c9d7e4a-3b2f-48d1-9a7c-5e2b4f8d1a22', 'P002', 'Utility Payment', 'BILLING', true, NOW(6)),
       ('9a2f4d7c-6b1e-45f8-8c2d-7e4a1b3c5d33', 'P003', 'Game Voucher', 'DIGITAL', true, NOW(6)),
       ('2d7a1c4e-8f3b-4d9a-b2c1-6e7f4a3b2c44', 'P004', 'Internet Package', 'TELCO', true, NOW(6)),
       ('5e1b3d7a-4c9f-46a2-8d1e-3f7a2b4c5d55', 'P005', 'Cinema Ticket', 'ENTERTAINMENT', true, NOW(6)),
       ('8c4d2a7f-1e3b-49d5-a6c2-4b7e1d3f6a66', 'P006', 'Food Delivery', 'FOOD', true, NOW(6)),
       ('3f7a2c1d-5e4b-47a9-8d3c-1b6e4f2a7d77', 'P007', 'Transport Card', 'TRANSPORT', true, NOW(6)),
       ('6b2d4f8a-7c1e-4a3d-9f2b-5e1c7a4d8f88', 'P008', 'Marketplace Order', 'ECOM', true, NOW(6));

INSERT INTO merchants (id, code, name, mcc, country, city, is_active, created_at)
VALUES ('a14f3c7d-92b1-4e5a-8c3d-1f2a7b4c9d01', 'M001', 'Shop A', '5411', 'KZ', 'Almaty', true, NOW(6)),
       ('b25e4d8a-13c7-4f6b-9d2a-2e3b8c5d1f02', 'M002', 'Shop B', '5732', 'KZ', 'Astana', true, NOW(6)),
       ('c36a5e9b-24d8-4a7c-8e3b-3f4c9d6a2b03', 'M003', 'Shop C', '5812', 'KZ', 'Shymkent', true, NOW(6)),
       ('d47b6f1c-35e9-4b8d-9f4c-4a5d1e7b3c04', 'M004', 'Shop D', '5999', 'KZ', 'Karaganda', true, NOW(6)),
       ('e58c7a2d-46f1-4c9e-8a5d-5b6e2f8c4d05', 'M005', 'Shop E', '4900', 'KZ', 'Aktobe', true, NOW(6));

INSERT INTO agents (id, code, name, type, country, is_active, created_at)
VALUES ('f69d8b3e-57a2-4d1f-9b6e-6c7f3a9d5e11', 'A01', 'Mobile App', 'APP', 'KZ', true, NOW(6)),
       ('0a7e9c4f-68b3-4e2a-8c7f-7d8a4b1e6f22', 'A02', 'POS Partner', 'POS', 'KZ', true, NOW(6)),
       ('1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33', 'A03', 'Web Checkout', 'WEB', 'KZ', true, NOW(6));

INSERT INTO users (id, external_id, email, full_name, phone, country, created_at)
VALUES (UUID(), 'U-0001', 'user1@mail.com', 'User One', '+77010000001', 'KZ', NOW(6)),
       (UUID(), 'U-0002', 'user2@mail.com', 'User Two', '+77010000002', 'KZ', NOW(6)),
       (UUID(), 'U-0003', 'user3@mail.com', 'User Three', '+77010000003', 'KZ', NOW(6)),
       (UUID(), 'U-0004', 'user4@mail.com', 'User Four', '+77010000004', 'KZ', NOW(6)),
       (UUID(), 'U-0005', 'user5@mail.com', 'User Five', '+77010000005', 'KZ', NOW(6)),
       (UUID(), 'U-0006', 'user6@mail.com', 'User Six', '+77010000006', 'KZ', NOW(6)),
       (UUID(), 'U-0007', 'user7@mail.com', 'User Seven', '+77010000007', 'KZ', NOW(6)),
       (UUID(), 'U-0008', 'user8@mail.com', 'User Eight', '+77010000008', 'KZ', NOW(6)),
       (UUID(), 'U-0009', 'user9@mail.com', 'User Nine', '+77010000009', 'KZ', NOW(6)),
       (UUID(), 'U-0010', 'user10@mail.com', 'User Ten', '+77010000010', 'KZ', NOW(6)),
       (UUID(), 'U-0011', 'user11@mail.com', 'User Eleven', '+77010000011', 'KZ', NOW(6)),
       (UUID(), 'U-0012', 'user12@mail.com', 'User Twelve', '+77010000012', 'KZ', NOW(6)),
       (UUID(), 'U-0013', 'user13@mail.com', 'User Thirteen', '+77010000013', 'KZ', NOW(6)),
       (UUID(), 'U-0014', 'user14@mail.com', 'User Fourteen', '+77010000014', 'KZ', NOW(6)),
       (UUID(), 'U-0015', 'user15@mail.com', 'User Fifteen', '+77010000015', 'KZ', NOW(6)),
       (UUID(), 'U-0016', 'user16@mail.com', 'User Sixteen', '+77010000016', 'KZ', NOW(6)),
       (UUID(), 'U-0017', 'user17@mail.com', 'User Seventeen', '+77010000017', 'KZ', NOW(6)),
       (UUID(), 'U-0018', 'user18@mail.com', 'User Eighteen', '+77010000018', 'KZ', NOW(6)),
       (UUID(), 'U-0019', 'user19@mail.com', 'User Nineteen', '+77010000019', 'KZ', NOW(6)),
       (UUID(), 'U-0020', 'user20@mail.com', 'User Twenty', '+77010000020', 'KZ', NOW(6));

INSERT INTO merchant_products (merchant_id, product_id)
SELECT m.id, p.id
FROM merchants m
         CROSS JOIN products p;

INSERT INTO agent_merchants (agent_id, merchant_id)
SELECT a.id, m.id
FROM agents a
         CROSS JOIN merchants m;

INSERT INTO fee (id, merchant_id, agent_id, product_id, currency, calc_type, rate, fixed_amount, min_amount, max_amount,
                 is_active, valid_from, valid_to)
VALUES (UUID(), 'a14f3c7d-92b1-4e5a-8c3d-1f2a7b4c9d01', 'f69d8b3e-57a2-4d1f-9b6e-6c7f3a9d5e11',
        '7f4a3b2e-1d9c-4f7a-8b3e-2c1d9e7f4a11', 'KZT', 'PERCENT', 0.015, NULL, 50, 500, true,
        DATE_SUB(NOW(), INTERVAL 30 DAY), NULL),
       (UUID(), 'b25e4d8a-13c7-4f6b-9d2a-2e3b8c5d1f02', '0a7e9c4f-68b3-4e2a-8c7f-7d8a4b1e6f22',
        '1c9d7e4a-3b2f-48d1-9a7c-5e2b4f8d1a22', 'KZT', 'FIXED', NULL, 100, 50, 300, true,
        DATE_SUB(NOW(), INTERVAL 20 DAY), NULL),
       (UUID(), 'c36a5e9b-24d8-4a7c-8e3b-3f4c9d6a2b03', '1b8f1d5a-79c4-4f3b-9d8a-8e9b5c2f7a33',
        '9a2f4d7c-6b1e-45f8-8c2d-7e4a1b3c5d33', 'KZT', 'PERCENT', 0.020, NULL, 50, 600, true,
        DATE_SUB(NOW(), INTERVAL 25 DAY), NULL);

INSERT INTO transactions (id, tx_id, created_at, finished_at, buyer_user_id, merchant_id, agent_id, product_id,
                          amount, currency, status, result_code, success, rrn, stan, payment_method)
SELECT UUID(),
       CONCAT('TX-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(FLOOR(RAND() * 100000), 6, '0')),
       DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
       DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) + INTERVAL 5 SECOND,
       u.id,
       m.id,
       a.id,
       p.id,
       ROUND(RAND() * 20000 + 1000, 2),
       'KZT',
       IF(RAND() < 0.7, 'SUCCESS', 'FAILED'),
       IF(RAND() < 0.7, 'APPROVED', 'DECLINED'),
       IF(RAND() < 0.7, true, false),
       LPAD(FLOOR(RAND() * 999999999999), 12, '0'),
       LPAD(FLOOR(RAND() * 999999), 6, '0'),
       'CARD'
FROM users u
         JOIN merchants m
         JOIN agents a
         JOIN products p
LIMIT 200;