# InsertStore schema

# --- !Ups

INSERT INTO products (name, price_amount, price_currency, quantity) VALUES ('leather jacket',250000,0,3);
INSERT INTO products (name, price_amount, price_currency, quantity) VALUES ('blue jeans',150000,0,5);
INSERT INTO products (name, price_amount, price_currency, quantity) VALUES ('Dr. Martens boots',480000,0,1);
INSERT INTO products (name, price_amount, price_currency, quantity) VALUES ('running shoes',460000,0,0);


# --- !Downs

DELETE FROM products;