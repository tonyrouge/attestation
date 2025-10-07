package com.resetsa.homeworks;

import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Comparator;

import com.resetsa.homeworks.model.Customer;
import com.resetsa.homeworks.model.Order;
import com.resetsa.homeworks.model.Product;
import com.resetsa.homeworks.model.OrderStatus;
import com.resetsa.homeworks.service.CustomerService;
import com.resetsa.homeworks.service.OrderService;
import com.resetsa.homeworks.service.OrderStatusService;
import com.resetsa.homeworks.service.ProductService;

import java.io.IOException;

public class App {

public DBConfig config;

public App(Path path) {
this.config = new DBConfig(path);
}

public void run() throws SQLException {
try {
this.config.read();
} catch (IOException e) {
System.out.println(e);
}
FlywayMigration.migrateDatabase(config);
Connection conn = null;
try {
conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
conn.setAutoCommit(false);

ProductService ps = new ProductService(conn);
System.out.println("Создание нового продукта");
var productA = new Product(100, "Мега Вещь", 1000.00, 99, "Хлам");
System.out.println(productA);
ps.update(productA);

System.out.println("Создание нового клиента");
CustomerService cs = new CustomerService(conn);
var customerA = new Customer(100, "Vasya", "Pupkin", "+792454415", "vpup@mail.ru");
System.out.println(customerA);
cs.update(customerA);

OrderStatusService osc = new OrderStatusService(conn);
var statusList = osc.findAll();
Optional<OrderStatus> statusA = statusList.get().stream().filter(x -> x.getId() == 1).findFirst();

System.out.println("Создание нового заказа");
var orderA = new Order(100, customerA, productA, LocalDateTime.now(), 10, statusA.get());
OrderService os = new OrderService(conn);
os.update(orderA);
System.out.println(orderA);

System.out.println("Отображение последних 5 заказов");
var orders = os.findAll(cs, ps, osc);
if (orders.isPresent()) {
orders.get().stream()
.sorted(Comparator.comparing(Order::getOrderDate).reversed())
.limit(5)
.forEach(x -> System.out.printf("%s,%s,%s,%s%n",x.getId(),x.getCustomer().getLastname(),x.getProduct().getDescr(),x.getOrderDate()));
}

System.out.println("Обновление цены товара и кол-ва");
productA.setCount(productA.getCount()-10);
productA.setPrice(10000.55);
System.out.println(productA);
ps.update(productA);

System.out.println("Удаление тестовых записей");
os.delete(orderA);
ps.delete(productA);
cs.delete(customerA);

conn.commit();
System.out.println("Операции успешно завершены");
} catch (SQLException e) {
if (conn != null) {
conn.rollback();
System.out.println("Rollback transaction");
System.out.println(e);
}
} finally {
if (conn != null) {conn.close();}
}
}
}
