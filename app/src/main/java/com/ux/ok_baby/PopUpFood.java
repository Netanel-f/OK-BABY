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
import com.ux.ok_baby.Model.FoodEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Calendar;

import static com.ux.ok_baby.Constants.DATE_PATTERN;

public class PopUpFood {
    private Context context;
    private FoodEntry foodEntry;
    private DateTimePicker dateTimePicker;
    private Calendar myCalendar = Calendar.getInstance();
    private EditText dateET, startTimeET, endTimeET;
    private Button typeB, sideB, amountB;
    private PopupWindow popupWindow;
    private View popupView;
    private String babyID;

    public PopUpFood(Context context, String babyID) {
        this.context = context;
        this.babyID = babyID;
    }

    public void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_window_food, null);
        popupWindow = setupPopup(view, popupView);
        foodEntry = new FoodEntry();
        dateTimePicker = new DateTimePicker(context);

        dateET = popupView.findViewById(R.id.date);
        startTimeET = popupView.findViewById(R.id.startTime);
        endTimeET = popupView.findViewById(R.id.endTime);
        typeB = popupView.findViewById(R.id.type);
        sideB = popupView.findViewById(R.id.side);
        amountB = popupView.findViewById(R.id.amount);

        setUpDate();
        setUpTime(startTimeET);
        setUpTime(endTimeET);
        onTypeClick();
        onSideClick();
        onAmountClick();

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
                if (isDateValid(foodEntry.getDate()) && isTimeValid(foodEntry.getEndTime()) && isTimeValid(foodEntry.getStartTime())) { // TODO: 1/12/2020 check validation of other variables
                    CollectionReference foodCollection = FirebaseFirestore.getInstance().collection("babies").document(babyID).collection("food_reports");
                    String id = foodCollection.document().getId();
                    foodCollection.document(id).set(foodEntry);
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
                popup.getMenuInflater().inflate(R.menu.food_type_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.bottle) {
                            popupView.findViewById(R.id.sideLayout).setVisibility(View.GONE);
                            popupView.findViewById(R.id.amountLayout).setVisibility(View.VISIBLE);
                            typeB.setText("Bottle");
                            foodEntry.setType("Bottle");
                            foodEntry.setSide(null);
                        } else {
                            popupView.findViewById(R.id.sideLayout).setVisibility(View.VISIBLE);
                            popupView.findViewById(R.id.amountLayout).setVisibility(View.GONE);
                            typeB.setText("Breastfeeding");
                            foodEntry.setType("Breastfeeding");
                            foodEntry.setAmount(null);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private void onSideClick() {
        sideB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.food_side_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.right) {
                            sideB.setText("Right");
                            foodEntry.setSide("Right");
                        } else {
                            sideB.setText("Left");
                            foodEntry.setSide("Left");
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }


    private void onAmountClick() {
        amountB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.food_amount_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //TODO update amount menu.
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
                foodEntry.setDate(dateET.getText().toString());
            }
        });
    }


    private void setUpTime(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.timePicker(editText);
                if (v.getId() == R.id.endTime)
                    foodEntry.setEndTime(editText.getText().toString());
                else
                    foodEntry.setStartTime(editText.getText().toString());
            }
        });
    }
}
