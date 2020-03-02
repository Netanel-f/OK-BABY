package com.ux.ok_baby.model;

public abstract class ReportEntry {

    String entryID;

    /**
     * @param fieldNum - number of the field.
     * @return data according the the number of field.
     */
    public abstract String getDataByField(int fieldNum);

    /**
     * @return Number of the fields to be displayed in the table.
     */
    public abstract int getNumOfDisplayedFields();

    /**
     * @return date of the entry.
     */
    public abstract String getDate();

    /**
     * @return starting time of the entry.
     */
    public abstract String getStartTime();
}
