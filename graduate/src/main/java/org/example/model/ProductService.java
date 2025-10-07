package com.resetsa.homeworks.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import com.resetsa.homeworks.model.Product;

public class ProductService extends DbService{

public ProductService(Connection conn) {
super(conn);
}

public void update(Product product) throws SQLException {
String sql = """
INSERT INTO products (id, descr, price, count, category)
VALUES (?, ?, ?, ?, ?)
ON CONFLICT (id)
DO UPDATE SET
descr = EXCLUDED.descr,
price = EXCLUDED.price,
count = EXCLUDED.count,
category = EXCLUDED.category;
""";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, product.getId());
pstmt.setString(2, product.getDescr());
pstmt.setDouble(3, product.getPrice());
pstmt.setInt(4, product.getCount());
pstmt.setString(5, product.getCategory());
pstmt.executeUpdate();
}
}

public void delete(Product product) throws SQLException {
String sql = "delete from products where id = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, product.getId());
pstmt.executeUpdate();
}
}

private Product readProduct(ResultSet rs) throws SQLException {
var productId = rs.getInt("id");
var productDescr = rs.getString("descr");
var productPrice = rs.getDouble("price");
var productCount = rs.getInt("count");
var productCategory = rs.getString("category");
return new Product(productId, productDescr, productPrice, productCount, productCategory);
}
public Optional<Product> findById(Integer id) throws SQLException {
String sql = "select id,descr,price,count,category from products where id = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, id);
ResultSet rs = pstmt.executeQuery();
if (rs.next()) {
Product p = readProduct(rs);
return Optional
.of(p);
}
return Optional.empty();
}
}

public Optional<List<Product>> findAll() throws SQLException {
List<Product> result = new ArrayList<>();
String sql = "select id,descr,price,count,category from products";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
ResultSet rs = pstmt.executeQuery();
while (rs.next()) {
Product p = readProduct(rs);
result.add(p);
}
return Optional.of(result);
}
}
}
