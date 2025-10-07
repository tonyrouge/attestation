package com.resetsa.homeworks.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.resetsa.homeworks.model.Customer;

public class CustomerService extends DbService {

public CustomerService(Connection conn) {
super(conn);
}

private Customer readCustomer(ResultSet rs) throws SQLException {
var customerId = rs.getInt("id");
var customerName = rs.getString("name");
var customerLastname = rs.getString("lastname");
var customerPhone = rs.getString("phone");
var customerMail = rs.getString("mail");
return new Customer(customerId,customerName,customerLastname,customerPhone,customerMail);
}

public Optional<Customer> findById(Integer id) throws SQLException {
String sql = "select id,name,lastname,phone,mail from customers where id = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, id);
ResultSet rs = pstmt.executeQuery();
if (rs.next()) {
Customer c = readCustomer(rs);
return Optional
.of(c);
}
return Optional.empty();
}
}

public Optional<List<Customer>> findAll() throws SQLException {
List<Customer> result = new ArrayList<>();
String sql = "select id,name,lastname,phone,mail from customers";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
ResultSet rs = pstmt.executeQuery();
while (rs.next()) {
Customer p = readCustomer(rs);
result.add(p);
}
return Optional.of(result);
}
}

public void delete(Customer customer) throws SQLException {
String sql = "delete from customers where id = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, customer.getId());
pstmt.executeUpdate();
}
}

public void update(Customer customer) throws SQLException {
String sql = """
INSERT INTO customers (id, name, lastname, phone, mail)
VALUES (?, ?, ?, ?, ?)
ON CONFLICT (id)
DO UPDATE SET
name = EXCLUDED.name,
lastname = EXCLUDED.lastname,
phone = EXCLUDED.phone,
mail = EXCLUDED.mail;
""";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, customer.getId());
pstmt.setString(2, customer.getName());
pstmt.setString(3, customer.getLastname());
pstmt.setString(4, customer.getPhone());
pstmt.setString(5, customer.getMail());
pstmt.executeUpdate();
}
}
}
