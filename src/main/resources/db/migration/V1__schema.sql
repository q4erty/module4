create extension if not exists pgcrypto;

create table products
(
    id         uuid         not null default gen_random_uuid(),
    code       varchar(64)  not null,
    name       varchar(255) not null,
    category   varchar(100) not null,
    is_active  boolean      not null default true,
    created_at timestamp(6) not null default current_timestamp,
    primary key (id),
    constraint uk_products_code unique (code)
);

create table users
(
    id          uuid         not null default gen_random_uuid(),
    external_id varchar(100),
    email       varchar(255) not null,
    full_name   varchar(255) not null,
    phone       varchar(50),
    country     varchar(10),
    created_at  timestamp(6) not null default current_timestamp,
    primary key (id),
    constraint uk_users_external_id unique (external_id),
    constraint uk_users_email unique (email)
);

create table merchants
(
    id         uuid         not null default gen_random_uuid(),
    code       varchar(64)  not null,
    name       varchar(255) not null,
    mcc        varchar(10),
    country    varchar(10),
    city       varchar(100),
    is_active  boolean      not null default true,
    created_at timestamp(6) not null default current_timestamp,
    primary key (id),
    constraint uk_merchants_code unique (code)
);

create table agents
(
    id         uuid         not null default gen_random_uuid(),
    code       varchar(64)  not null,
    name       varchar(255) not null,
    type       varchar(32)  not null,
    country    varchar(10),
    is_active  boolean      not null default true,
    created_at timestamp(6) not null default current_timestamp,
    primary key (id),
    constraint uk_agents_code unique (code)
);

create table fee
(
    id           uuid         not null default gen_random_uuid(),
    merchant_id  uuid         not null,
    agent_id     uuid         not null,
    product_id   uuid         not null,
    currency     varchar(10)  not null,
    calc_type    varchar(32)  not null,
    rate         numeric(10, 4),
    fixed_amount numeric(18, 2),
    min_amount   numeric(18, 2),
    max_amount   numeric(18, 2),
    is_active    boolean      not null default true,
    valid_from   timestamp(6) not null default current_timestamp,
    valid_to     timestamp(6),
    primary key (id),
    constraint fk_fee_merchant foreign key (merchant_id) references merchants (id),
    constraint fk_fee_agent foreign key (agent_id) references agents (id),
    constraint fk_fee_product foreign key (product_id) references products (id),
    constraint chk_fee_calc_type check (calc_type in ('PERCENT', 'FIXED', 'PERCENT_WITH_CAP'))
);

create table transactions
(
    id             uuid           not null default gen_random_uuid(),
    tx_id          varchar(100)   not null,
    created_at     timestamp(6)   not null default current_timestamp,
    finished_at    timestamp(6),
    buyer_user_id  uuid           not null,
    merchant_id    uuid           not null,
    agent_id       uuid           not null,
    product_id     uuid           not null,
    amount         numeric(18, 2) not null,
    currency       varchar(10)    not null,
    status         varchar(32)    not null,
    result_code    varchar(32),
    success        boolean        not null,
    rrn            varchar(32),
    stan           varchar(32),
    payment_method varchar(50),
    primary key (id),
    constraint uk_transactions_tx_id unique (tx_id),
    constraint fk_tx_user foreign key (buyer_user_id) references users (id),
    constraint fk_tx_merchant foreign key (merchant_id) references merchants (id),
    constraint fk_tx_agent foreign key (agent_id) references agents (id),
    constraint fk_tx_product foreign key (product_id) references products (id),
    constraint chk_transactions_status check (status in ('NEW', 'PROCESSING', 'SUCCESS', 'FAILED', 'CANCELED')),
    constraint chk_transactions_result_code check (result_code is null or result_code in ('APPROVED', 'DECLINED'))
);

create table merchant_products
(
    merchant_id uuid not null,
    product_id  uuid not null,
    primary key (merchant_id, product_id),
    constraint fk_mp_merchant foreign key (merchant_id) references merchants (id),
    constraint fk_mp_product foreign key (product_id) references products (id)
);

create table agent_merchants
(
    agent_id    uuid not null,
    merchant_id uuid not null,
    primary key (agent_id, merchant_id),
    constraint fk_am_agent foreign key (agent_id) references agents (id),
    constraint fk_am_merchant foreign key (merchant_id) references merchants (id)
);

create index idx_transactions_created_at on transactions (created_at);
create index idx_transactions_status on transactions (status);
create index idx_transactions_success on transactions (success);
create index idx_transactions_currency on transactions (currency);
create index idx_transactions_merchant_id on transactions (merchant_id);
create index idx_transactions_agent_id on transactions (agent_id);
create index idx_transactions_product_id on transactions (product_id);

create index idx_fee_lookup
    on fee (merchant_id, agent_id, product_id, currency, is_active, valid_from, valid_to);