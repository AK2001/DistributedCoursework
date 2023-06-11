package com.example.babymonitoringcw.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfiguration {
    private static final String DB_PROPERTIES = "database.properties";

    private String url;
    private String username;
    private String password;
    private String driver;

    public DatabaseConfiguration() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(DB_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(input);

            url = properties.getProperty("database.url");
            username = properties.getProperty("database.username");
            password = properties.getProperty("database.password");
            driver = properties.getProperty("database.driver");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }
}
