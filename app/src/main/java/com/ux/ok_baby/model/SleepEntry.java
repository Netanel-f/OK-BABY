package com.ux.ok_baby.model;

public class SleepEntry extends ReportEntry{
    private String date, StartTime, endTime;

    public SleepEntry(String date, String startTime, String endTime) {
        this.date = date;
        this.StartTime = startTime;
        this.endTime = endTime;
    }

    public SleepEntry() {
    }

    public String getDate() {
        return date;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }
}
