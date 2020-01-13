package com.ux.ok_baby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimePicker {
    private Calendar myCalendar = Calendar.getInstance();
    private Context context;

    public DateTimePicker(Context context) {
        this.context = context;
    }

    public void datePicker(final EditText editText) {
        new DatePickerDialog(context, getDateSetListener(editText), myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DatePickerDialog.OnDateSetListener getDateSetListener(final EditText editText) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US); // TODO: 1/10/2020 change US
                editText.setText(sdf.format(myCalendar.getTime()));
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener getTimeSetListener(final EditText editText) {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                editText.setText(selectedHour + ":" + selectedMinute);
            }
        };
    }


    public void timePicker(final EditText editText) {
        new TimePickerDialog(context, getTimeSetListener(editText),
                myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
    }
}
