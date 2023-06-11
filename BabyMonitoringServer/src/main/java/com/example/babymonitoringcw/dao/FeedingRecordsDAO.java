package com.example.babymonitoringcw.dao;

import com.example.babymonitoringcw.Configuration.DatabaseConfiguration;
import com.example.babymonitoringcw.Model.FeedingRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedingRecordsDAO {

    private final DatabaseConfiguration databaseConfiguration;

    public FeedingRecordsDAO(){
        databaseConfiguration = new DatabaseConfiguration();
    }

    private Connection openConnection() {
        Connection connection;
        try {
            Class.forName(databaseConfiguration.getDriver());
            String URL = databaseConfiguration.getUrl();
            String NAME = databaseConfiguration.getUsername();
            String PASSWORD = databaseConfiguration.getPassword();
            connection = DriverManager.getConnection(URL, NAME, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private void closeConnection(Connection connection) {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // CREATE
    public void addFeedingRecord(float milkConsumption, Timestamp startTime, Timestamp endTime) {
        Connection connection = openConnection();

        String query = "INSERT INTO feeding_records(milk, sessionstarttime, sessionendtime) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setFloat(1, milkConsumption);
            stmt.setTimestamp(2, startTime);
            stmt.setTimestamp(3, endTime);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    // UPDATE
    public void updateFeedingRecord(int id, float milkConsumption, Timestamp startTime, Timestamp endTime) {
        Connection connection = openConnection();
        String query = "UPDATE feeding_records SET milk = ?, sessionstarttime = ?, sessionendtime = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setFloat(1, milkConsumption);
            stmt.setTimestamp(2, startTime);
            stmt.setTimestamp(3, endTime);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    // DELETE
    public void deleteFeedingRecord(int id) {
        Connection connection = openConnection();
        String query = "delete from feeding_records where id= ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    // READ OPERATIONS
    public List<FeedingRecord> listFeedingRecord(Timestamp from, Timestamp to) {
        Connection connection = openConnection();
        String query;

        if (from == null && to != null) {
            query = "SELECT * FROM feeding_records WHERE sessionendtime <= ? ORDER BY id";
        } else if (from != null && to == null) {
            query = "SELECT * FROM feeding_records WHERE sessionstarttime >= ? ORDER BY id";
        } else if (from == null) {
            query = "SELECT * FROM feeding_records ORDER BY id";
        } else {
            query = "SELECT * FROM feeding_records WHERE sessionstarttime >= ? AND sessionendtime <= ? ORDER BY id";
        }

        List<FeedingRecord> allFeedingRecords = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            if (from != null) {
                stmt.setTimestamp(1, from);
                if (to != null) {
                    stmt.setTimestamp(2, to);
                }
            } else if (to != null) {
                stmt.setTimestamp(1, to);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    allFeedingRecords.add(
                            new FeedingRecord(
                                    rs.getInt("id"),
                                    rs.getFloat("milk"),
                                    rs.getTimestamp("sessionstarttime"),
                                    rs.getTimestamp("sessionendtime")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }

        return allFeedingRecords;
    }

    public double getAverageMilkConsumption(Timestamp from, Timestamp to) {
        Connection connection = openConnection();
        String query;

        if (from == null && to != null) {
            query = "SELECT AVG(milk) AS averageConsumption FROM feeding_records WHERE sessionendtime <= ?";
        } else if (from != null && to == null) {
            query = "SELECT AVG(milk) AS averageConsumption FROM feeding_records WHERE sessionstarttime >= ?";
        } else if (from == null) {
            query = "SELECT AVG(milk) AS averageConsumption FROM feeding_records";
        } else {
            query = "SELECT AVG(milk) AS averageConsumption FROM feeding_records WHERE sessionstarttime >= ? AND sessionendtime <= ?";
        }

        double averageConsumption = -1.0;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            if (from != null) {
                stmt.setTimestamp(1, from);
                if (to != null) {
                    stmt.setTimestamp(2, to);
                }
            } else if (to != null) {
                stmt.setTimestamp(1, to);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    averageConsumption = rs.getDouble("averageConsumption");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }

        return averageConsumption;
    }

    public double getAverageFeedingSessionDuration() {
        Connection connection = openConnection();
        String query = "SELECT AVG(EXTRACT(EPOCH FROM (SessionEndTime - sessionstarttime)) / 60) AS avgduration FROM feeding_records";

        double averageDuration = -1.0;
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    averageDuration = rs.getDouble("avgduration");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }

        return averageDuration;
    }

}
