package com.ux.ok_baby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



/////// TODO
//////     Button btn = view.findViewById(R.id.btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopUpSleep popUpClass = new PopUpSleep(getActivity());
//                popUpClass.showPopupWindow(view);
//            }
//        });
public class PopUpSleep {
    private Context context;
    private Calendar myCalendar = Calendar.getInstance();


    public PopUpSleep(Context context) {
        this.context = context;
    }

    public void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_sleep, null);
        final PopupWindow popupWindow = setupPopup(view, popupView);

        datePicker((EditText) popupView.findViewById(R.id.date));
        timePicker((EditText) popupView.findViewById(R.id.startTime));
        timePicker((EditText) popupView.findViewById(R.id.endTime));


        popupView.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        popupView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private PopupWindow setupPopup(View view, View popupView) {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    private void datePicker(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, getDateSetListener(editText), myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
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


    private void timePicker(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(context, getTimeSetListener(editText),
                        myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }
}
