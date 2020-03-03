package com.ux.ok_baby.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ux.ok_baby.utils.Constants.TIME_PATTERN;

/**
 * Represents a sleep report entry.
 */
public class SleepEntry extends com.ux.ok_baby.model.ReportEntry {
    private String date, startTime, endTime;
    private String strDuration;
    private long duration;

    /**
     * Constructor with date, start time and end time.
     */
    public SleepEntry(String date, String startTime, String endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = calculateDurationInt(this.startTime, this.endTime);
    }


    /**
     * Constructor with details.
     * @param date - date of the entry.
     * @param startTime - starting time of the entry.
     * @param endTime - ending time of the entry.
     * @param strDuration - the duration of the entry as string.
     */
    public SleepEntry(String date, String startTime, String endTime, String strDuration) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.strDuration = strDuration;
    }

    /**
     * Empty sleep entry constructor.
     */
    public SleepEntry() {
        this.date = "";
        this.startTime = "";
        this.endTime = "";
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
     * @return end time.
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime .
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return start time.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime .
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return validity of entry.
     */
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

    /**
     * Calculate the duration from two given times.
     * @param time1 - first given time.
     * @param time2 - second given time.
     * @return duration.
     */
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

    /**
     * Prettify the duration string: number to hours/minutes.
     * @param duration - number in minutes.
     * @return duration in minutes/hours.
     */
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

    /**
     * Updates and returns the duration of the entry.
     * @return duration.
     */
    public long getDuration() {
        if (duration == 0) {
            duration = calculateDurationInt(getStartTime(), getEndTime());
        }
        return duration;
    }

    /**
     * @return duration string.
     */
    private String getDurationString() {
        if (strDuration == null || strDuration.isEmpty()) {
            strDuration = duartionToString(getDuration());
        }
        return strDuration;
    }

    /**
     * @param duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }
}
