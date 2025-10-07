package com.resetsa.homeworks.model;

import java.time.LocalDateTime;

public class Order {

private Integer id;
private Customer customer;
private Product product;
private LocalDateTime orderDate;
private Integer count;
private OrderStatus status;

public Order(Integer id, Customer customer, Product product, LocalDateTime orderDate, Integer count,
OrderStatus status) {
this.id = id;
this.customer = customer;
this.product = product;
this.orderDate = orderDate;
this.count = count;
this.status = status;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Customer getCustomer() {
return customer;
}

public void setCustomer(Customer customer) {
this.customer = customer;
}

public Product getProduct() {
return product;
}

public void setProduct(Product product) {
this.product = product;
}

public LocalDateTime getOrderDate() {
return orderDate;
}

public void setOrderDate(LocalDateTime orderDate) {
this.orderDate = orderDate;
}

public Integer getCount() {
return count;
}

public void setCount(Integer count) {
this.count = count;
}

public OrderStatus getStatus() {
return status;
}

public void setStatus(OrderStatus status) {
this.status = status;
}

@Override
public String toString() {
return "Order [id=" + id + ", customer=" + customer + ", product=" + product + ", orderDate=" + orderDate
+ ", count=" + count + ", status=" + status + "]";
}

}
