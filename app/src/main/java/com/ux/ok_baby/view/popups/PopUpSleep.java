package com.ux.ok_baby.view.popups;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ux.ok_baby.R;
import com.ux.ok_baby.model.SleepEntry;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

public class PopUpSleep {
    private String babyID;
    private View popupView;
    private Context context;
    private SleepEntry sleepEntry;
    private PopupWindow popupWindow;
    private DateTimePicker dateTimePicker;
    private EntriesViewModel entriesViewModel;
    private EditText dateET, startTimeET, endTimeET;

    public PopUpSleep(Context context, String babyID, EntriesViewModel entriesViewModel) {
        this.context = context;
        this.babyID = babyID;
        this.entriesViewModel = entriesViewModel;
    }

    public void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_window_sleep, null);
        popupWindow = setupPopup(view, popupView);
        sleepEntry = new SleepEntry();
        dateTimePicker = new DateTimePicker(context);
        dateET = popupView.findViewById(R.id.date);
        startTimeET = popupView.findViewById(R.id.startTime);
        endTimeET = popupView.findViewById(R.id.endTime);

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
                updateSleepEntryObject();
                if (sleepEntry.isValidEntry()) {
                    entriesViewModel.addSleepEntry(babyID, sleepEntry);
                    popupWindow.dismiss();
                } else
                    Toast.makeText(context, "One or more fields are empty.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateSleepEntryObject() {
        sleepEntry.setDate(dateET.getText().toString());
        sleepEntry.setEndTime(endTimeET.getText().toString());
        sleepEntry.setStartTime(startTimeET.getText().toString());
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
            }
        });
    }

    private void setUpTime(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.timePicker(editText);
            }
        });
    }
}
