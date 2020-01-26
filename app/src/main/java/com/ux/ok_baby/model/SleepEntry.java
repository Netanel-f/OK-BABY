package com.ux.ok_baby.model;

public class SleepEntry extends com.ux.ok_baby.model.ReportEntry {
    private String date, startTime, endTime;

    public SleepEntry(String date, String startTime, String endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public SleepEntry() {
        this.date = "";
        this.startTime = "";
        this.endTime = "";
    }

    public String getDate() {
        return date;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isValidEntry() {
        return !date.isEmpty() && !endTime.isEmpty() && !startTime.isEmpty();
    }
}
