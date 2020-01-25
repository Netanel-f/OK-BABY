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

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getColor() {
        return color;
    }

    public String getTexture() {
        return texture;
    }

    public String getTime() {
        return time;
    }

    public boolean isValidEntry() {
        if (date.isEmpty() || time.isEmpty() || type.isEmpty())
            return false;
        if (type.equals(POO))
            return !texture.isEmpty(); //TODO check color validation
        return true;
    }
}
