package com.example.babymonitoringcw.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String DB_INITIALIZATION = "database-init.sql";

    public static void main(String[] args){

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

        try{
            Class.forName(databaseConfiguration.getDriver());

            Connection connection = DriverManager.getConnection(
                    databaseConfiguration.getUrl(),
                    databaseConfiguration.getUsername(),
                    databaseConfiguration.getPassword()
            );

            runInitScript(connection);

            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runInitScript(Connection connection){
        try (InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(DB_INITIALIZATION);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String line;
            StringBuilder script = new StringBuilder();

            // Read the SQL script line by line
            while ((line = reader.readLine()) != null) {
                // Ignore empty lines and comments
                if (!line.trim().isEmpty() && !line.trim().startsWith("--")) {
                    script.append(line);

                    // If the line ends with a semicolon, execute the statement
                    if (line.trim().endsWith(";")) {
                        executeQuery(connection, script.toString());
                        script.setLength(0);
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeQuery(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
