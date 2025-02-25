package main.java.RedisDataLoader;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PostgresExtractor {

    private static class DatabaseConfig {
        String url;
        String username;
        String password;
        String query;
    }

    private static DatabaseConfig parseDatabaseConfig(Map<String, Object> config) {
        return new DatabaseConfig() {{
            url = (String) config.get("db.url");
            username = (String) config.get("db.username");
            password = (String) config.get("db.password");
            //TODO добавить новые запросы
            query = (String) config.get("db.query");
        }};
    }

    public static Map<String, Object> loadDatabaseData(Map<String, Object> config) {
        DatabaseConfig dbConfig = parseDatabaseConfig(config);
        Map<String, Object> dbData = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(
                dbConfig.url,
                dbConfig.username,
                dbConfig.password)) {

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(dbConfig.query)) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        dbData.put(columnName, value);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while reading data form DB", e);
        }

        return dbData;
    }
}

