package com.ux.ok_baby.view.popups;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.ux.ok_baby.utils.Constants.*;

public class DateTimePicker {
    private Context context;
    private Calendar myCalendar = Calendar.getInstance();

    public DateTimePicker(Context context) {
        this.context = context;
    }


    public String getCurTime() {
        return new SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(new Date());
    }

    public String getCurDate() {
        return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(new Date());
    }

    public void datePicker(final TextView textView) {
        new DatePickerDialog(context, getDateSetListener(textView), myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DatePickerDialog.OnDateSetListener getDateSetListener(final TextView textView) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
                textView.setText(sdf.format(myCalendar.getTime()));
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener getTimeSetListener(final TextView textView) {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                myCalendar.set(Calendar.MINUTE, selectedMinute);
                myCalendar.set(Calendar.SECOND, 0);
                SimpleDateFormat df = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
                textView.setText(df.format(myCalendar.getTime()));
            }
        };
    }

    public void timePicker(final TextView textView) {
        new TimePickerDialog(context, getTimeSetListener(textView),
                myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
    }
}
