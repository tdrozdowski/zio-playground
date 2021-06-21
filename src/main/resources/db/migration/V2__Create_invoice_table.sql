create table if not exists invoices (
    id serial primary key,
    user_id int not null,
    total decimal not null,
    paid boolean default false,
    created_at timestamp not null
);

create table if not exists invoices_items (
    invoice_id int not null,
    item_id int not null
);