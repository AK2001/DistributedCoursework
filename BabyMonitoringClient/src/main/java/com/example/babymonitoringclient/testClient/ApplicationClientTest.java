package com.example.babymonitoringclient.testClient;

import com.example.babymonitoringclient.model.FeedingRecord;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;
import java.sql.Timestamp;
import java.util.List;

public class ApplicationClientTest {
    private static final String BASE_URL = "http://localhost:8080/BabyMonitoringServer_war_exploded/api";

    // "Tests" each operation
    public static void main(String[] args) {
        // Create a JAX-RS client
        Client client = ClientBuilder.newClient();

        // Create WebTarget for the base URL
        WebTarget baseTarget = client.target(BASE_URL);

        // Define the specific endpoint URL for the milkData resource
        WebTarget feedingRecordTarget = baseTarget.path("/milkData");

        // Define the username and password
        String username = "admin";
        String password = "admin123";

        // Encode the username and password in Base64
        String authHeaderValue = createAuthHeader(username, password);

        System.out.println("TESTING ADD");
        // Add a FeedingRecord record
        addFeedingRecord(feedingRecordTarget,authHeaderValue);

        System.out.println("\nTESTING UPDATE");
        // Update a feeding record
        updateFeedingRecord(feedingRecordTarget,authHeaderValue);

        System.out.println("\nTESTING DELETE");
        // Delete a feeding record
        deleteFeedingRecord(feedingRecordTarget,authHeaderValue);

        System.out.println("\nTESTING LIST ALL");
        // Get all feeding records within a time range
        getAllFeedingRecords(feedingRecordTarget,authHeaderValue);

        System.out.println("\nTESTING AVG MILK");
        // Get average milk consumption within a time range
        getAverageMilkConsumption(feedingRecordTarget,authHeaderValue);

        System.out.println("\nTESTING AVG DURATION");
        // Get average feeding session duration
        getAverageFeedingSessionDuration(feedingRecordTarget,authHeaderValue);

        // Close the client
        client.close();
    }

    // Creates a authentication header
    private static String createAuthHeader(String username, String password) {
        String authString = username + ":" + password;
        byte[] authBytes = authString.getBytes();
        byte[] encodedAuthBytes = Base64.encodeBase64(authBytes);
        return "Basic " + new String(encodedAuthBytes);
    }

    // Performs ADD operation
    private static void addFeedingRecord(WebTarget target, String authHeader) {

        // Create new Record
        float amountOfMilk = 250.50f;
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());

        // Build the request payload
        Invocation.Builder invocationBuilder = target.path("/add")
                .queryParam("amount", amountOfMilk)
                .queryParam("from", startTime)
                .queryParam("to", toTime)
                .request();

        // Put header
        invocationBuilder.header(HttpHeaders.AUTHORIZATION, authHeader);

        // Send POST request
        Response response = invocationBuilder.post(Entity.json(null));

        // Handle the response
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Feeding record added successfully");
        } else {
            System.out.println("Failed to add feeding record: " + response.readEntity(String.class));
        }
    }

    // Performs UPDATE operation
    private static void updateFeedingRecord(WebTarget target, String authHeader) {
        // Update an existing record
        int recordID = 1;
        float amountOfMilk = 600.0f;
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());

        // Build the request payload
        Invocation.Builder invocationBuilder = target.path("/update")
                .queryParam("id", recordID)
                .queryParam("amount", amountOfMilk)
                .queryParam("from", startTime)
                .queryParam("to", toTime)
                .request();

        // Put header
        invocationBuilder.header(HttpHeaders.AUTHORIZATION, authHeader);

        // PUT request requires a json payload, so we append an empty payload
        JsonObject payload = Json.createObjectBuilder().build();

        // Send PUT request
        Response response = invocationBuilder.put(Entity.json(payload));

        // Handle the response
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Feeding record updated successfully");
        } else {
            System.out.println("Failed to update feeding record: " + response.readEntity(String.class));
        }
    }

    // Performs DELETE operation
    private static void deleteFeedingRecord(WebTarget target, String authHeader) {
        // Delete an existing record
        int recordID = 4; // NOTE: Make sure record with ID:3 exists, else change this value

        // Build the request path
        WebTarget deleteTarget = target.path("/delete/" + recordID);

        // Build the request
        Invocation.Builder invocationBuilder = deleteTarget.request();

        // Put header
        invocationBuilder.header(HttpHeaders.AUTHORIZATION, authHeader);

        // Send the DELETE request to delete the milkData record
        Response response = invocationBuilder.delete();

        // Handle the response
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Feeding record with ID: " + recordID + " deleted successfully");
        } else {
            System.out.println("Failed to delete feeding record with ID: " + recordID + ", Response: " + response.readEntity(String.class));
        }
    }

    // Performs READ operation
    private static void getAllFeedingRecords(WebTarget target, String authHeader) {
        // Get all milkData records within a time range

        // USE ONLY WHEN RECORDS ARE WITHING THESE DATES
        // ELSE the results will be an empty list
        // By DEFAULT this method returns all records
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());

        // Build the request payload
        Invocation.Builder invocationBuilder = target.path("/list")
                .queryParam("from", "")
                .queryParam("to", "")
                .request();

        // Put header
        invocationBuilder.header(HttpHeaders.AUTHORIZATION, authHeader);

        // Send the POST request to get all milkData records
        Response response = invocationBuilder.get();

        // Handle the response
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            List<FeedingRecord> allData = response.readEntity(List.class);
            System.out.println("All feeding records: " + allData);
        } else {
            System.out.println("Failed to get feeding records: " + response.readEntity(String.class));
        }
    }

    // Performs READ operation
    private static void getAverageMilkConsumption(WebTarget target, String authHeader) {
        // Get average milk consumption within a time range

        // USE ONLY WHEN RECORDS ARE WITHING THESE DATES
        // ELSE the results will be an empty list
        // By DEFAULT this method returns avg consumption of all records
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());

        // Build the request payload
        Invocation.Builder invocationBuilder = target.path("/getAverageConsumption")
                .queryParam("from", "")
                .queryParam("to", "")
                .request();

        // Put header
        invocationBuilder.header(HttpHeaders.AUTHORIZATION, authHeader);

        // Send the POST request to get the average milk consumption
        Response response = invocationBuilder.get();

        // Handle the response
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            double averageConsumption = response.readEntity(Double.class);
            System.out.println("Average milk consumption: " + averageConsumption);
        } else {
            System.out.println("Failed to get average milk consumption: " + response.readEntity(String.class));
        }
    }

    // Performs READ operation
    private static void getAverageFeedingSessionDuration(WebTarget target, String authHeader) {
        // Get average feeding session duration
        Invocation.Builder invocationBuilder = target.path("/getAverageFeedingSessionDuration")
                .request();

        // Put header
        invocationBuilder.header(HttpHeaders.AUTHORIZATION, authHeader);

        // Send the GET request to get the average feeding session duration
        Response response = invocationBuilder.get();

        // Handle the response
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            double averageDuration = response.readEntity(Double.class);
            System.out.println("Average feeding session duration: " + averageDuration);
        } else {
            System.out.println("Failed to get average feeding session duration: " + response.readEntity(String.class));
        }
    }
}
