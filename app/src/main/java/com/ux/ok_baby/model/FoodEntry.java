package com.ux.ok_baby.model;

import static com.ux.ok_baby.utils.Constants.*;

public class FoodEntry extends com.ux.ok_baby.model.ReportEntry {
    private String date, endTime, startTime, type, side, amount;

    public FoodEntry(String date, String endTime, String startTime, String type, String side, String amount) {
        this.date = date;
        this.endTime = endTime;
        this.startTime = startTime;
        this.amount = amount;
        this.side = side;
        this.type = type;
    }

    public FoodEntry() {
        this.date = "";
        this.endTime = "";
        this.startTime = "";
        this.type = "";
        this.side = "";
        this.amount = "";
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getSide() {
        return side;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isValidEntry() {
        if (date.isEmpty() || endTime.isEmpty() || startTime.isEmpty() || type.isEmpty())
            return false;
        if (type.equals(BOTTLE))
            return !amount.isEmpty();
        else
            return !side.isEmpty();
    }
}
