package com.resetsa.homeworks.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.resetsa.homeworks.model.OrderStatus;

public class OrderStatusService extends DbService {

public OrderStatusService(Connection conn) {
super(conn);
}

private OrderStatus readOrderStatus(ResultSet rs) throws SQLException {
var oId = rs.getInt("id");
var oStatus = rs.getString("status");
return new OrderStatus(oId,oStatus);
}

public Optional<OrderStatus> findById(Integer id) throws SQLException {
String sql = "select id,status from order_status where id = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
pstmt.setInt(1, id);
ResultSet rs = pstmt.executeQuery();
if (rs.next()) {
OrderStatus os = readOrderStatus(rs);
return Optional
.of(os);
}
return Optional.empty();
}
}

public Optional<List<OrderStatus>> findAll() throws SQLException {
List<OrderStatus> result = new ArrayList<>();
String sql = "select id,status from order_status";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
ResultSet rs = pstmt.executeQuery();
while (rs.next()) {
OrderStatus o = readOrderStatus(rs);
result.add(o);
}
return Optional.of(result);
}
}
}
