package com.example.babymonitoringclient.model;

import java.sql.Timestamp;

public class FeedingRecord {
    private int id;
    private float milkConsumed;
    private Timestamp startTime;
    private Timestamp finishTime;

    public FeedingRecord(int id, float milkConsumed, Timestamp startTime, Timestamp finishTime) {
        this.id = id;
        this.milkConsumed = milkConsumed;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}
