package com.resetsa.homeworks;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

public static final String configPath = "homeworks/final-task/application.properties";

public static void main(String[] args) {
Path config = Paths.get(configPath);
var app = new App(config);
try {
app.run();
}
catch (Exception e) {
System.out.println("Error: "+e.getMessage());
}
}
}
