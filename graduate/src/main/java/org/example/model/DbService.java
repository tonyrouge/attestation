package com.resetsa.homeworks.service;

import java.sql.Connection;

public class DbService {

protected Connection conn;

public DbService(Connection conn) {
this.conn = conn;
}

public Connection getConn() {
return conn;
}

}
