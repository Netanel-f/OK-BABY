package com.ux.ok_baby.model;

import static com.ux.ok_baby.utils.Constants.*;

public class FoodEntry extends com.ux.ok_baby.model.ReportEntry {
    private String date, startTime, endTime, type, side, amount;

    public FoodEntry(String date, String startTime, String endTime, String type, String side, String amount) {
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

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getType() {
        return type;
    }

    public String getSide() {
        return side;
    }

    public String getAmount() {
        return amount;
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

    public String getDataByField(int fieldNum){
        switch (fieldNum){
            case 0:
                return getDate();
            case 1:
                return getStartTime();
            case 2:
                return getEndTime();
            case 3:
                return getType();
            case 4:
                return getSide();
            case 5:
                return getAmount();
            default:
                return null;
        }
    }
}
