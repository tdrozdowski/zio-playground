create table item (
                      id serial primary key,
                      name varchar(255) not null,
                      description text not null,
                      unit_price decimal not null,
                      created_at timestamp not null
)