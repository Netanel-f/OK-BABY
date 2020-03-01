package com.ux.ok_baby.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ux.ok_baby.utils.Constants.TIME_PATTERN;

public class SleepEntry extends com.ux.ok_baby.model.ReportEntry {
    private String date, startTime, endTime;
    private String strDuration;
    private long duration;

    public SleepEntry(String date, String startTime, String endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = calculateDurationInt(this.startTime, this.endTime);
    }

    public SleepEntry(String date, String startTime, String endTime, String strDuration) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.strDuration = strDuration;
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

    @Override
    public String getDataByField(int fieldNum) {
        switch (fieldNum) {
            case 0:
                return getDate();
            case 1:
                return getDurationString();
            case 2:
                return getStartTime();
            case 3:
                return getEndTime();
            default:
                return null;
        }
    }

    @Override
    public int getNumOfDisplayedFields() {
        return 4;
    }

    private long calculateDurationInt(String time1, String time2) {
        SimpleDateFormat format = new SimpleDateFormat(TIME_PATTERN);
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
            long diff = date2.getTime() - date1.getTime();
            return diff / (60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String duartionToString(long duration) {
        long diffMinutes = duration;
        if (diffMinutes > 60) {
            diffMinutes = diffMinutes % 60;
            long diffHours = duration / (60) % 24;
            String mins;
            if (diffMinutes < 10) {
                mins = "0" + diffMinutes;
            } else {
                mins = "" + diffMinutes;
            }

            return diffHours + ":" + mins + " hrs";
        } else {
            return diffMinutes + " mins";
        }
    }

    public long getDuration() {
        if (duration == 0) {
            duration = calculateDurationInt(getStartTime(), getEndTime());
        }
        return duration;
    }

    private String getDurationString() {
        if (strDuration == null || strDuration.isEmpty()) {
            strDuration = duartionToString(getDuration());
        }
        return strDuration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
