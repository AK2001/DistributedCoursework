package com.example.babymonitoringcw.resources;

import com.example.babymonitoringcw.Model.FeedingRecord;
import com.example.babymonitoringcw.dao.FeedingRecordsDAO;
import com.example.babymonitoringcw.services.LineChart;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.annotation.security.RolesAllowed;
import java.sql.Timestamp;
import java.util.List;

@Path("/milkData")
public class FeedingRecordsResource {

    FeedingRecordsDAO feedingRecordsDAO = new FeedingRecordsDAO();

    // ADD
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response insertFeedingRecord(
            @QueryParam("amount") float amountOfMilk,
            @QueryParam("from") Timestamp startTime,
            @QueryParam("to") Timestamp toTime
    ) {

        try {
            feedingRecordsDAO.addFeedingRecord(amountOfMilk, startTime, toTime);
            return Response.status(Response.Status.OK).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot add record").build();
        }
    }

    // UPDATE
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response updateFeedingRecord(
            @QueryParam("id") int recordID,
            @QueryParam("amount") float amountOfMilk,
            @QueryParam("from") Timestamp startTime,
            @QueryParam("to") Timestamp toTime
    ) {
        try {
            feedingRecordsDAO.updateFeedingRecord(recordID, amountOfMilk, startTime, toTime);
            return Response.status(Response.Status.OK).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot update record").build();
        }
    }

    // DELETE
    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteFeedingRecord(
            @PathParam("id") int recordID
    ) {
        try {
            feedingRecordsDAO.deleteFeedingRecord(recordID);
            return Response.status(Response.Status.OK).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot update record").build();
        }
    }

    // READ OPERATIONS
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "PHYSICIAN"})
    public Response displayAllFeedingRecord(
            @QueryParam("from") Timestamp startTime,
            @QueryParam("to") Timestamp toTime
    ) {

        List<FeedingRecord> allData = feedingRecordsDAO.listFeedingRecord(startTime, toTime);

        if (allData.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No data available").build();
        }
        return Response.status(Response.Status.OK).entity(allData).build();
    }


    @GET
    @Path("/getAverageConsumption")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "PHYSICIAN"})
    public Response getAverageMilkConsumption(
            @QueryParam("from") Timestamp startTime,
            @QueryParam("to") Timestamp toTime
    ) {
        double averageConsumption = feedingRecordsDAO.getAverageMilkConsumption(startTime, toTime);
        if (averageConsumption < 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("No data available").build();
        }
        return Response.status(Response.Status.OK).entity(averageConsumption).build();
    }

    @GET
    @Path("/getAverageFeedingSessionDuration")
    @RolesAllowed({"ADMIN", "PHYSICIAN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAverageFeedingSessionDuration() {
        double averageDuration = feedingRecordsDAO.getAverageFeedingSessionDuration();
        if (averageDuration < 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("No data available").build();
        }
        return Response.status(Response.Status.OK).entity(averageDuration).build();
    }

    // Chart
    @GET
    @Path("/chart")
    @Produces("image/png")
    @RolesAllowed({"ADMIN", "PHYSICIAN"})
    public Response getChart(
            @QueryParam("from") Timestamp startTime,
            @QueryParam("to") Timestamp toTime
    ) {
        System.out.println(startTime + " " + toTime);
        LineChart lineChart = new LineChart(feedingRecordsDAO, startTime, toTime);
        byte[] chartAsIMG = lineChart.getChartImagePNG();
        return Response.status(Response.Status.OK).entity(chartAsIMG).build();
    }
}