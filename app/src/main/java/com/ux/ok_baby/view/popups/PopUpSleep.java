package com.ux.ok_baby.view.popups;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
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
    private TextView dateTV, startTimeTV, endTimeTV;

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

        setUpEntry();
        setUpAddButton();
        setUpExit();
    }

    private void setUpEntry() {
        dateTV = popupView.findViewById(R.id.date);
        startTimeTV = popupView.findViewById(R.id.startTime);
        endTimeTV = popupView.findViewById(R.id.endTime);

        setUpDate();
        setUpStartTime();
        setUpEndTime();
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
                    entriesViewModel.setIsFirstTimeSleep(true);
                    popupWindow.dismiss();
                } else
                    Toast.makeText(context, "One or more fields are empty.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateSleepEntryObject() {
        sleepEntry.setDate(dateTV.getText().toString());
        sleepEntry.setEndTime(endTimeTV.getText().toString());
        sleepEntry.setStartTime(startTimeTV.getText().toString());
    }

    private PopupWindow setupPopup(View view, View popupView) {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    private void setUpDate() {
        dateTV.setText(dateTimePicker.getCurDate());
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.datePicker(dateTV);
            }
        });
    }

    private void setUpEndTime() {
        endTimeTV.setText(dateTimePicker.getCurTime());
        endTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.timePicker(endTimeTV);
            }
        });
    }

    private void setUpStartTime() {
        startTimeTV.setText(dateTimePicker.getCurTime());
        startTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.timePicker(startTimeTV);
            }
        });
        onStartTimeTextChanged();
    }

    private void onStartTimeTextChanged() {
        startTimeTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                endTimeTV.setText(startTimeTV.getText());
            }
        });
    }
}
