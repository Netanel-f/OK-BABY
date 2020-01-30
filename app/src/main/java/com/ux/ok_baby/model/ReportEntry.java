package com.ux.ok_baby.model;

public abstract class ReportEntry {

    String entryID;

    public abstract String getDataByField(int fieldNum);

    public abstract int getNumOfDisplayedFields();
}
