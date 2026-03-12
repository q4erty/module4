create table products
(
    id         char(36)     not null,
    code       varchar(64)  not null,
    name       varchar(255) not null,
    category   varchar(100) not null,
    is_active  boolean      not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    unique key uk_products_code (code)
);

create table users
(
    id          char(36)     not null,
    external_id varchar(100) null,
    email       varchar(255) not null,
    full_name   varchar(255) not null,
    phone       varchar(50) null,
    country     varchar(10) null,
    created_at  datetime(6) not null default current_timestamp(6),
    primary key (id),
    unique key uk_users_external_id (external_id),
    unique key uk_users_email (email)
);

create table merchants
(
    id         char(36)     not null,
    code       varchar(64)  not null,
    name       varchar(255) not null,
    mcc        varchar(10) null,
    country    varchar(10) null,
    city       varchar(100) null,
    is_active  boolean      not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    unique key uk_merchants_code (code)
);

create table agents
(
    id         char(36)     not null,
    code       varchar(64)  not null,
    name       varchar(255) not null,
    type       varchar(32)  not null,
    country    varchar(10) null,
    is_active  boolean      not null default true,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    unique key uk_agents_code (code)
);

create table fee
(
    id           char(36)    not null,
    merchant_id  char(36)    not null,
    agent_id     char(36)    not null,
    product_id   char(36)    not null,
    currency     varchar(10) not null,
    calc_type    varchar(32) not null,
    rate         decimal(10, 4) null,
    fixed_amount decimal(18, 2) null,
    min_amount   decimal(18, 2) null,
    max_amount   decimal(18, 2) null,
    is_active    boolean     not null default true,
    valid_from   datetime(6) not null default current_timestamp(6),
    valid_to     datetime(6) null,
    primary key (id),
    constraint fk_fee_merchant foreign key (merchant_id) references merchants (id),
    constraint fk_fee_agent foreign key (agent_id) references agents (id),
    constraint fk_fee_product foreign key (product_id) references products (id),
    constraint chk_fee_calc_type check (calc_type in ('PERCENT', 'FIXED', 'PERCENT_WITH_CAP'))
);

create table transactions
(
    id             char(36)       not null,
    tx_id          varchar(100)   not null,
    created_at     datetime(6) not null default current_timestamp(6),
    finished_at    datetime(6) null,
    buyer_user_id  char(36)       not null,
    merchant_id    char(36)       not null,
    agent_id       char(36)       not null,
    product_id     char(36)       not null,
    amount         decimal(18, 2) not null,
    currency       varchar(10)    not null,
    status         varchar(32)    not null,
    result_code    varchar(32) null,
    success        boolean        not null,
    rrn            varchar(32) null,
    stan           varchar(32) null,
    payment_method varchar(50) null,
    primary key (id),
    unique key uk_transactions_tx_id (tx_id),
    constraint fk_tx_user foreign key (buyer_user_id) references users (id),
    constraint fk_tx_merchant foreign key (merchant_id) references merchants (id),
    constraint fk_tx_agent foreign key (agent_id) references agents (id),
    constraint fk_tx_product foreign key (product_id) references products (id),
    constraint chk_transactions_status check (status in ('NEW', 'PROCESSING', 'SUCCESS', 'FAILED', 'CANCELED')),
    constraint chk_transactions_result_code check (result_code is null or result_code in ('APPROVED', 'DECLINED'))
);

create table merchant_products
(
    merchant_id char(36) not null,
    product_id  char(36) not null,
    primary key (merchant_id, product_id),
    constraint fk_mp_merchant foreign key (merchant_id) references merchants (id),
    constraint fk_mp_product foreign key (product_id) references products (id)
);

create table agent_merchants
(
    agent_id    char(36) not null,
    merchant_id char(36) not null,
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

create index idx_fee_lookup on fee (merchant_id, agent_id, product_id, currency, is_active, valid_from, valid_to);