CREATE TABLE IF NOT EXISTS products (
id SERIAL PRIMARY KEY,
descr TEXT NOT NULL,
price BIGINT NOT NULL CHECK (price >= 0),
count INT NOT NULL CHECK (count >= 0),
category VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS customers (
id SERIAL PRIMARY KEY,
name TEXT NOT NULL,
lastname TEXT NOT NULL,
phone VARCHAR(64) NOT NULL,
mail TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_status (
id SERIAL PRIMARY KEY,
status TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
id SERIAL PRIMARY KEY,
product_id INTEGER NOT NULL,
customer_id INTEGER NOT NULL,
date_order DATE NOT NULL,
count NUMERIC NOT NULL CHECK (count > 0),
status_order INTEGER NOT NULL,
CONSTRAINT c_order 
FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (status_order) REFERENCES order_status (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX product_id_idx ON orders(product_id);
CREATE INDEX customer_id_idx ON orders(customer_id);
CREATE INDEX status_order_idx ON orders(status_order);
CREATE INDEX date_order_idx ON orders(date_order);
