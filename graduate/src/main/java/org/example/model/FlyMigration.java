package com.resetsa.homeworks;

import org.flywaydb.core.Flyway;

public class FlywayMigration {

public static void migrateDatabase(DBConfig config) {
// Конфигурация Flyway
Flyway flyway = Flyway.configure().dataSource(config.getUrl(), config.getUsername(), config.getPassword())
.locations("classpath:db/migration").baselineOnMigrate(true).load();

// Запуск миграции
flyway.migrate();
System.out.println("Migration OK");
}
}
