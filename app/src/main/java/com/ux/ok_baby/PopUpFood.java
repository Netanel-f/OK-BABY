package com.ux.ok_baby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import androidx.appcompat.widget.PopupMenu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PopUpFood {
    private Context context;
    private Calendar myCalendar = Calendar.getInstance();


    public PopUpFood(Context context) {
        this.context = context;
    }

    public void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_food, null);
        final PopupWindow popupWindow = setupPopup(view, popupView);

        datePicker((EditText) popupView.findViewById(R.id.date));
        timePicker((EditText) popupView.findViewById(R.id.startTime));
        timePicker((EditText) popupView.findViewById(R.id.endTime));
        onTypeClick((Button) popupView.findViewById(R.id.type), popupView);
        onSideClick((Button) popupView.findViewById(R.id.side), popupView);
        onAmountClick((Button) popupView.findViewById(R.id.amount), popupView);

        setUpButtons(popupView, popupWindow);
    }


    private void onTypeClick(final Button button, final View popupView) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.food_type_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.bottle) {
                            popupView.findViewById(R.id.sideLayout).setVisibility(View.GONE);
                            popupView.findViewById(R.id.amountLayout).setVisibility(View.VISIBLE);
                            button.setText("Bottle");
                        } else {
                            popupView.findViewById(R.id.sideLayout).setVisibility(View.VISIBLE);
                            popupView.findViewById(R.id.amountLayout).setVisibility(View.GONE);
                            button.setText("Breastfeeding");
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private void onSideClick(final Button button, final View popupView) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.food_side_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.right)
                            button.setText("Right");
                        else
                            button.setText("Left");
                        return true;
                    }
                });
                popup.show();
            }
        });
    }


    private void onAmountClick(final Button button, final View popupView) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.food_side_menu, popup.getMenu());
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


    private void setUpButtons(View popupView, final PopupWindow popupWindow) {
        popupView.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add report
            }
        });

        popupView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    private PopupWindow setupPopup(View view, View popupView) {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
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
