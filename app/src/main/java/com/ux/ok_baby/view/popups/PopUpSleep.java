package com.ux.ok_baby.view.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.SleepEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import static com.ux.ok_baby.utils.Constants.DATE_PATTERN;


public class PopUpSleep {
    private Context context;
    private View popupView;
    private PopupWindow popupWindow;
    private DateTimePicker dateTimePicker;
    private SleepEntry sleepEntry;
    private EditText dateET, startTimeET, endTimeET;
    private String babyID;

    public PopUpSleep(Context context, String babyID) {
        this.context = context;
        this.babyID = babyID;
    }

    public void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_window_sleep, null);
        popupWindow = setupPopup(view, popupView);
        sleepEntry = new SleepEntry();
        dateTimePicker = new DateTimePicker(context);

        dateET = (EditText) popupView.findViewById(R.id.date);
        startTimeET = (EditText) popupView.findViewById(R.id.startTime);
        endTimeET = (EditText) popupView.findViewById(R.id.endTime);

        setUpDate();
        setUpTime(startTimeET);
        setUpTime(endTimeET);
        setUpAddButton();
        setUpExit();
    }

    private void setUpExit() {
        popupView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void setUpAddButton() {
        popupView.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDateValid(sleepEntry.getDate()) && isTimeValid(sleepEntry.getEndTime()) && isTimeValid(sleepEntry.getStartTime())) {
                    CollectionReference sleepCollection = FirebaseFirestore.getInstance()
                            .collection("babies").document(babyID).collection("sleep_reports");
                    String id = sleepCollection.document().getId();
                    sleepCollection.document(id).set(sleepEntry);
                } else {
                    Toast.makeText(context, "One or more fields are incorrect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private boolean isDateValid(String dobString) {
        if (dobString.isEmpty()) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        try {
            sdf.parse(dobString);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private boolean isTimeValid(String time) {
        if (time.isEmpty()) {
            return false;
        }
        DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalTime.parse(time, strictTimeFormatter);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private PopupWindow setupPopup(View view, View popupView) {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    private void setUpDate() {
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.datePicker(dateET);
                sleepEntry.setDate(dateET.getText().toString());
            }
        });
    }

    private void setUpTime(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.timePicker(editText);
                if (v.getId() == R.id.endTime)
                    sleepEntry.setEndTime(editText.getText().toString());
                else
                    sleepEntry.setStartTime(editText.getText().toString());
            }
        });
    }
}
