package com.ux.ok_baby.model;

import static com.ux.ok_baby.utils.Constants.*;

/**
 * Represents a food report entry.
 */
public class FoodEntry extends com.ux.ok_baby.model.ReportEntry {
    private String date, startTime, endTime, type, side, amount;

    /**
     * Food entry constructor.
     * @param date - date of the entry.
     * @param startTime - start time of the entry.
     * @param endTime - end time of the entry.
     * @param type - type of the food entry: bottle/breastfeeding.
     * @param side - side of breastfeeding.
     * @param amount - amount of bottle feed.
     */
    public FoodEntry(String date, String startTime, String endTime, String type, String side, String amount) {
        this.date = date;
        this.endTime = endTime;
        this.startTime = startTime;
        this.amount = amount;
        this.side = side;
        this.type = type;
    }

    /**
     * Food entry constructor.
     * @param date - date of the entry.
     * @param startTime - start time of the entry.
     * @param endTime - end time of the entry.
     * @param type - type of the food entry: bottle/breastfeeding.
     * @param amount - amount of bottle feed.
     */
    public FoodEntry(String date, String startTime, String endTime, String type, String amount) {
        this.date = date;
        this.endTime = endTime;
        this.startTime = startTime;
        this.amount = amount;
        this.type = type;
    }

    /**
     * Empty FoodEntry constructor.
     */
    public FoodEntry() {
        this.date = "";
        this.endTime = "";
        this.startTime = "";
        this.type = "";
        this.side = "";
        this.amount = "";
    }

    /**
     * @return date.
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date .
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return start time.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime start time.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return end time.
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime end time.
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return type: bottle/breastfeeding.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type: bottle/breastfeeding.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return side of breastfeeding.
     */
    public String getSide() {
        return side;
    }

    /**
     * @param side - side of breastfeeding.
     */
    public void setSide(String side) {
        this.side = side;
    }

    /**
     * @return amount of bottle feed.
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount of bottle feed.
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return validity of entry.
     */
    public boolean isValidEntry() {
        if (date.isEmpty() || endTime.isEmpty() || startTime.isEmpty() || type.isEmpty())
            return false;
        if (type.equals(BOTTLE))
            return !amount.isEmpty();
        else
            return true;
    }

    /**
     * @return pretty version of the amount.
     */
    private String getAmountString() {
        if (type.equals(BOTTLE))
            return amount + " ml";
        return amount;
    }

    /**
     * @param fieldNum - number of the field.
     * @return data according the the number of field.
     */
    public String getDataByField(int fieldNum) {
        switch (fieldNum) {
            case 0:
                return getDate();
            case 1:
                return getType();
            case 2:
                return getStartTime();
            case 3:
                return getAmountString();
            case 4:
                return getSide();
            case 5:
                return getEndTime();
            default:
                return null;
        }
    }

    @Override
    public int getNumOfDisplayedFields() {
        return 4;
    }
}
