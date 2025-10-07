package com.resetsa.homeworks.model;

public class Product {

private Integer id;
private String descr;
private Double price;
private Integer count;
private String category;

public Product(Integer id, String descr, Double price, Integer count, String category) {
this.id = id;
this.descr = descr;
this.price = price;
this.count = count;
this.category = category;
}

@Override
public String toString() {
return "Product [id=" + id + ", descr=" + descr + ", price=" + price + ", count=" + count + ", category="
+ category + "]";
}

public Integer getId() {
return id;
}

public String getDescr() {
return descr;
}

public Double getPrice() {
return price;
}

public Integer getCount() {
return count;
}

public String getCategory() {
return category;
}

public void setId(Integer id) {
this.id = id;
}

public void setDescr(String descr) {
this.descr = descr;
}

public void setPrice(Double price) {
this.price = price;
}

public void setCount(Integer count) {
this.count = count;
}

public void setCategory(String category) {
this.category = category;
}

}
