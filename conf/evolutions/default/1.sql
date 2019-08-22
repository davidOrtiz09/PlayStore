# Store schema

# --- !Ups

CREATE TABLE products (
    id SERIAL NOT NULL,
    name varchar(80),
    price_amount NUMERIC(32,20) NOT NULL,
    price_currency integer NOT NULL,
    quantity integer NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY(name)
);

# --- !Downs

DROP TABLE products;