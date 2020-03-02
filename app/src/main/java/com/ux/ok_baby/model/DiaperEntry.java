package com.ux.ok_baby.model;

import static com.ux.ok_baby.utils.Constants.POO;

/**
 * Represents a diaper report entry.
 */
public class DiaperEntry extends com.ux.ok_baby.model.ReportEntry {

    private String date, time, type, texture, color;

    /**
     * DiaperEntry constructor.
     * @param date - date of the entry.
     * @param time - time of diaper change.
     * @param type - type of diaper, pee/poo.
     * @param texture - texture of the poo.
     * @param color - color of the poo.
     */
    public DiaperEntry(String date, String time, String type, String texture, String color) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.texture = texture;
        this.color = color;
    }

    /**
     * DiaperEntry constructor.
     * @param date - date of the entry.
     * @param time - time of diaper change.
     * @param type - type of diaper, pee/poo.
     * @param texture - texture of the poo.
     */
    public DiaperEntry(String date, String time, String type, String texture) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.texture = texture;
    }

    /**
     * Empty constructor.
     */
    public DiaperEntry() {
    }

    /**
     * @return date of the entry.
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date of the entry.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return type of diaper entry.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type - type of diaper entry.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return time of the entry.
     */
    public String getTime() {
        return time;
    }

    /**
     * Implemented ReportEntry method.
     * @return time of the entry.
     */
    public String getStartTime() {
        // for comparator
        return time;
    }

    /**
     * @param time of the entry.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return texture of a poo entry.
     */
    public String getTexture() {
        return texture;
    }

    /**
     * @param texture of a poo entry.
     */
    public void setTexture(String texture) {
        this.texture = texture;
    }

    /**
     * @return color of the poo entry.
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color of the poo entry.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return validity of the entry.
     */
    public boolean isValidEntry() {
        if (date.isEmpty() || time.isEmpty() || type.isEmpty())
            return false;
        if (type.equals(POO))
            return !texture.isEmpty();
        return true;
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
        return 4;
    }
}
