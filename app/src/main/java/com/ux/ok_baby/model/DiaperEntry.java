package com.ux.ok_baby.model;

import static com.ux.ok_baby.utils.Constants.BOTTLE;
import static com.ux.ok_baby.utils.Constants.POO;

public class DiaperEntry extends com.ux.ok_baby.model.ReportEntry {

    private String date, time, type, texture, color;

    public DiaperEntry(String date, String time, String type, String texture, String color) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.texture = texture;
        this.color = color;
    }

    public DiaperEntry() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public String getStartTime(){
        // for comparator
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isValidEntry() {
        if (date.isEmpty() || time.isEmpty() || type.isEmpty())
            return false;
        if (type.equals(POO))
            return !texture.isEmpty(); //TODO check red_color_circle validation
        return true;
    }

    public String getDataByField(int fieldNum){
        switch (fieldNum){
            case 0:
                return getDate();
            case 1:
                return getType();
            case 2:
                return getTime();
            case 3:
                return getTexture();
            case 4:
                return getColor();
            default:
                return null;
        }
    }

    @Override
    public int getNumOfDisplayedFields() {
        return 5;
    }
}
