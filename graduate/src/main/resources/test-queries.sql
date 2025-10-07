-- выбор всех клиентов c фамилией П*
SELECT * FROM customers WHERE lastname LIKE 'П%';

-- выбор товаров по кол-ву на складе
SELECT descr,count FROM products ORDER BY count;

-- выбор всех заказов с сортировкой по дате
SELECT
c.name,
c.lastname,
p.descr,
os.status
FROM
orders AS o
JOIN customers AS c ON o.customer_id = c.id
JOIN products AS p ON o.product_id = p.id
JOIN order_status AS os ON o.status_order = os.id
ORDER BY
o.date_order;

-- Подсчет кол-ва заказов в статусе Новый или В обработке
SELECT count(*) FROM orders as o
JOIN
order_status AS os ON o.status_order = os.id
WHERE
os.status = 'Новый' OR os.status = 'В обработке';

-- Оценка стоимости товаров на складе
SELECT SUM(count * price) AS total_sum
FROM products;

-- Уменьшить кол-во товара с именем "Планшет IPad Air 5" на 1
UPDATE products SET count = count - 1 WHERE descr = 'Планшет iPad Air 5';

-- Обновить статус покупок в диапазоне дат
UPDATE orders SET status_order = 7 WHERE date_order BETWEEN '2024-02-01' AND '2024-02-02';

-- Удалить отмененные заказы
DELETE FROM orders WHERE status_order = 9;

-- Удалить клиентов без заказов
DELETE FROM customers
WHERE id NOT IN (
SELECT DISTINCT customer_id
FROM orders 
);

-- Очистить таблицу заказов
TRUNCATE orders;
Customer.java
-- выбор всех клиентов c фамилией П*
SELECT * FROM customers WHERE lastname LIKE 'П%';

-- выбор товаров по кол-ву на складе
SELECT descr,count FROM products ORDER BY count;

-- выбор всех заказов с сортировкой по дате
SELECT
c.name,
c.lastname,
p.descr,
os.status
FROM
orders AS o
JOIN customers AS c ON o.customer_id = c.id
JOIN products AS p ON o.product_id = p.id
JOIN order_status AS os ON o.status_order = os.id
ORDER BY
o.date_order;

-- Подсчет кол-ва заказов в статусе Новый или В обработке
SELECT count(*) FROM orders as o
JOIN
order_status AS os ON o.status_order = os.id
WHERE
os.status = 'Новый' OR os.status = 'В обработке';

-- Оценка стоимости товаров на складе
SELECT SUM(count * price) AS total_sum
FROM products;

-- Уменьшить кол-во товара с именем "Планшет IPad Air 5" на 1
UPDATE products SET count = count - 1 WHERE descr = 'Планшет iPad Air 5';

-- Обновить статус покупок в диапазоне дат
UPDATE orders SET status_order = 7 WHERE date_order BETWEEN '2024-02-01' AND '2024-02-02';

-- Удалить отмененные заказы
DELETE FROM orders WHERE status_order = 9;

-- Удалить клиентов без заказов
DELETE FROM customers
WHERE id NOT IN (
SELECT DISTINCT customer_id
FROM orders 
);

-- Очистить таблицу заказов
TRUNCATE orders;