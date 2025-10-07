package com.resetsa.homeworks.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.resetsa.homeworks.model.Order;

public class OrderService extends DbService {

public OrderService(Connection conn) {
super(conn);
}

public void update(Order order) throws SQLException {
String sql = """
INSERT INTO orders (id, product_id, customer_id, date_order, count, status_order)
VALUES (?, ?, ?, ?, ?, ?)
ON CONFLICT (id)
DO UPDATE SET
product_id = EXCLUDED.product_id,
customer_id = EXCLUDED.customer_id,
date_order = EXCLUDED.date_order,
count = EXCLUDED.count,
status_order = EXCLUDED.status_order;
""";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, order.getId());
pstmt.setInt(2, order.getProduct().getId());
pstmt.setInt(3, order.getCustomer().getId());
pstmt.setTimestamp(4, Timestamp.valueOf(order.getOrderDate()));
pstmt.setInt(5, order.getCount());
pstmt.setInt(6, order.getStatus().getId());
pstmt.executeUpdate();
}
}

private Order readOrder(ResultSet rs,CustomerService cs,ProductService ps,OrderStatusService ost) throws SQLException {
var oId = rs.getInt("id");
var oProductId = rs.getInt("product_id");
var oCustomerId = rs.getInt("customer_id");
var oDateOrder = rs.getTimestamp("date_order").toLocalDateTime();
var oCount = rs.getInt("count");
var oStatusId = rs.getInt("status_order");
var oProduct = ps.findById(oProductId);
var oCustomer = cs.findById(oCustomerId);
var oStatusOrder = ost.findById(oStatusId);
return new Order(oId,oCustomer.get(),oProduct.get(),oDateOrder,oCount,oStatusOrder.get());
}
public Optional<List<Order>> findAll(CustomerService cs, ProductService ps, OrderStatusService ost) throws SQLException {
List<Order> result = new ArrayList<>();
String sql = "select id,product_id,customer_id,date_order,count,status_order from orders";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
ResultSet rs = pstmt.executeQuery();
while (rs.next()) {
Order o = readOrder(rs, cs,ps,ost);
result.add(o);
}
return Optional.of(result);
}
}

public Optional<Order> findById(Integer id, CustomerService cs, ProductService ps, OrderStatusService ost) throws SQLException {
String sql = "select id,product_id,customer_id,date_order,count,status_order from orders where id = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, id);
ResultSet rs = pstmt.executeQuery();
if (rs.next()) {
Order o = readOrder(rs, cs,ps,ost);
return Optional.of(o);
}
return Optional.empty();
}
}

public void delete(Order o) throws SQLException {
String sql = "delete from orders where id = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, o.getId());
pstmt.executeUpdate();
}
}
}
