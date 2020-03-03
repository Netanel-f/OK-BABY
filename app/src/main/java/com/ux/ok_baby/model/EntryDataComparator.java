package com.ux.ok_baby.model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import static com.ux.ok_baby.utils.Constants.DATE_PATTERN;
import static com.ux.ok_baby.utils.Constants.TIME_PATTERN;

/**
 * Date based comparator for ReportEntry objects.
 */
public class EntryDataComparator implements Comparator<ReportEntry> {

    private static final String TAG = "EntryDataComparator";

    @Override
    public int compare(ReportEntry o1, ReportEntry o2) {

        Date date1, date2;

        // handle title entry
        if (o1.getDate().equals("date")){
            return -1;
        } else if (o1.getDate().equals("date")){
            return 1;
        }

        // parse date
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        try {
            date1 = format.parse(o1.getDate());
            date2 = format.parse(o2.getDate());
        } catch (Exception e) {
            Log.d(TAG, "Invalid date format for comparing ");
            e.printStackTrace();
            return 0;
        }

        // descending order
        if (date1.after(date2)) {
            return -1;
        } else if (date1.before(date2)) {
            return 1;
        } else {
            // same day, compare hours
            return compareTime(o1.getStartTime(), o2.getStartTime());
        }
    }

    /**
     * Compare hours.
     * @param time1 - first time to compare.
     * @param time2 - second time to compare.
     * @return -1 if time1 is after time2, 1 if time 1 is before time2, 0 if equal.
     */
    private int compareTime(String time1, String time2) {
        SimpleDateFormat format = new SimpleDateFormat(TIME_PATTERN);
        Date date1, date2;

        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
        } catch (Exception e) {
            Log.d(TAG, "Invalid time format for comparing ");
            e.printStackTrace();
            return 0;
        }

        if (date1.after(date2)) {
            return -1;
        } else if (date1.before(date2)) {
            return 1;
        } else { // same time
            return 0;
        }
    }
}
