package com.ux.ok_baby;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ux.ok_baby.Model.DiaperEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Calendar;

import static com.ux.ok_baby.Constants.DATE_PATTERN;

public class PopUpDiaper {
    private Context context;
    private View popupView;
    private DiaperEntry diaperEntry;
    private DateTimePicker dateTimePicker;
    private PopupWindow popupWindow;
    private Calendar myCalendar = Calendar.getInstance();
    private String babyID;
    private EditText dateET, timeET, endTimeET;
    private Button typeB, textureB;


    public PopUpDiaper(Context context, String babyID) {
        this.context = context;
        this.babyID = babyID;
    }

    public void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_window_diaper, null);
        popupWindow = setupPopup(view, popupView);
        diaperEntry = new DiaperEntry();
        dateTimePicker = new DateTimePicker(context);

        dateET = popupView.findViewById(R.id.date);
        timeET = popupView.findViewById(R.id.time);
        typeB = popupView.findViewById(R.id.type);
        textureB = popupView.findViewById(R.id.texture);
        // TODO: 1/12/2020 update texture and color.
        setUpDate();
        setUpTime();
        onTypeClick();
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

                if (isDateValid(diaperEntry.getDate()) && isTimeValid(diaperEntry.getTime())) { // TODO: 1/12/2020 check validation of other variables
                    CollectionReference foodCollection = FirebaseFirestore.getInstance().collection("babies").document(babyID).collection("diaper_reports");
                    String id = foodCollection.document().getId();
                    foodCollection.document(id).set(diaperEntry);
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


    private void onTypeClick() {
        typeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.diaper_type_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.poo) {
                            popupView.findViewById(R.id.textureLayout).setVisibility(View.VISIBLE);
                            popupView.findViewById(R.id.colorLayout).setVisibility(View.VISIBLE);
                            typeB.setText("Poo");
                            diaperEntry.setType("Poo");
                        } else {
                            popupView.findViewById(R.id.textureLayout).setVisibility(View.GONE);
                            popupView.findViewById(R.id.colorLayout).setVisibility(View.GONE);
                            typeB.setText("Pee");
                            diaperEntry.setType("Pee");
                        }
                        return true;
                    }
                });
                popup.show();
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

    private void setUpDate() {
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.datePicker(dateET);
                diaperEntry.setDate(dateET.getText().toString());
            }
        });
    }

    private void setUpTime() {
        timeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.timePicker(timeET);
                diaperEntry.setTime(timeET.getText().toString());
            }
        });
    }
}

