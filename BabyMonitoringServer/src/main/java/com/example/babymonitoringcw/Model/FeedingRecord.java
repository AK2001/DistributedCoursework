package com.example.babymonitoringcw.Model;

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

    public int getId() {
        return id;
    }

    public float getMilkConsumed() {
        return milkConsumed;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }
}
