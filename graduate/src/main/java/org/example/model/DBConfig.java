package com.resetsa.homeworks;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

public class DBConfig {

public final String dbNamePrefix = "dbname";
public final String usernamePrefix = "username";
public final String passwordPrefix = "password";
public final String hostPrefix = "host";

private String dbName;
private String username;
private String password;
private String host = "";
private String delim = "=";
private Path path;

public DBConfig(Path path) {
this.path = path;
}

public DBConfig(Path path, String delim) {
this.path = path;
this.delim = delim;
}

public void read() throws IOException {
try (BufferedReader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8);) {
String line;
while ((line = reader.readLine()) != null) {
List<String> trimmedParts = Arrays.stream(line.split(this.delim)).map(String::trim)
.collect(Collectors.toList());
readDbName(trimmedParts);
readPassword(trimmedParts);
readUsername(trimmedParts);
}
}
}

public void readDbName(List<String> parts) {
if (parts.get(0).toLowerCase().equals(dbNamePrefix)) {
this.dbName = parts.get(1);
}
}

public void readUsername(List<String> parts) {
if (parts.get(0).toLowerCase().equals(usernamePrefix)) {
this.username = parts.get(1);
}
}

public void readPassword(List<String> parts) {
if (parts.get(0).toLowerCase().equals(passwordPrefix)) {
this.password = parts.get(1);
}
}

public void readHost(List<String> parts) {
if (parts.get(0).toLowerCase().equals(hostPrefix)) {
this.host = parts.get(1);
}
}

public String getDbName() {
return dbName;
}

public String getUsername() {
return username;
}

public String getPassword() {
return password;
}

public String getDelim() {
return delim;
}

public String getHost() {
if (this.host.isEmpty()) {
return "localhost";
}
return host;
}

public String getUrl() {
return String.format("jdbc:postgresql://%s:5432/%s", this.getHost(), this.getDbName());
}
}
