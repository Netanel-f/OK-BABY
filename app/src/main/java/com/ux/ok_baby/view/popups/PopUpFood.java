package com.ux.ok_baby.view.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ux.ok_baby.model.FoodEntry;
import com.ux.ok_baby.R;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import static com.ux.ok_baby.utils.Constants.*;

public class PopUpFood {
    private TextView mls;
    private String babyID;
    private View popupView;
    private Context context;
    private Spinner typeSpin, sideSpin;
    private FoodEntry foodEntry;
    private PopupWindow popupWindow;
    private DateTimePicker dateTimePicker;
    private EntriesViewModel entriesViewModel;
    private EditText dateET, startTimeET, endTimeET;

    public PopUpFood(Context context, String babyID, EntriesViewModel entriesViewModel) {
        this.babyID = babyID;
        this.context = context;
        this.entriesViewModel = entriesViewModel;
    }

    public void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_window_food, null);
        popupWindow = setupPopup(view, popupView);
        foodEntry = new FoodEntry();
        dateTimePicker = new DateTimePicker(context);

        setUpEntry();
        setUpAddButton();
        setUpExit();
    }

    private void setUpEntry() {
        dateET = popupView.findViewById(R.id.date);
        startTimeET = popupView.findViewById(R.id.startTime);
        endTimeET = popupView.findViewById(R.id.endTime);
        typeSpin = popupView.findViewById(R.id.type);
        sideSpin = popupView.findViewById(R.id.side);
        mls = popupView.findViewById(R.id.mls);

        setUpDate();
        setUpTime(startTimeET);
        setUpTime(endTimeET);
        setUpType();
        setUpSide();
        setUpAmount();
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
                updateFoodEntryObject();
                if (foodEntry.isValidEntry()) {
                    entriesViewModel.addFoodEntry(babyID, foodEntry);
                    popupWindow.dismiss();
                } else {
                    Toast.makeText(context, "One or more fields are empty", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateFoodEntryObject() {
        foodEntry.setDate(dateET.getText().toString());
        foodEntry.setEndTime(endTimeET.getText().toString());
        foodEntry.setStartTime(startTimeET.getText().toString());
        foodEntry.setType(typeSpin.getSelectedItem().toString());
        if (foodEntry.getType().equals(BOTTLE)) {
            foodEntry.setAmount(mls.getText().toString());
            foodEntry.setSide("");
        } else {
            foodEntry.setAmount("");
            foodEntry.setSide(sideSpin.getSelectedItem().toString());
        }
    }


    private void setUpType() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(context, R.array.food_type,
                        android.R.layout.simple_spinner_dropdown_item);
        typeSpin.setAdapter(adapter);
        typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String type = (String) adapterView.getItemAtPosition(i);
                if (type.equals(BOTTLE)) {
                    popupView.findViewById(R.id.sideLayout).setVisibility(View.GONE);
                    popupView.findViewById(R.id.amountLayout).setVisibility(View.VISIBLE);
                } else {
                    popupView.findViewById(R.id.sideLayout).setVisibility(View.VISIBLE);
                    popupView.findViewById(R.id.amountLayout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        typeSpin.setSelection(0);
    }

    private void setUpSide() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(context, R.array.food_side,
                        android.R.layout.simple_spinner_dropdown_item);
        sideSpin.setAdapter(adapter);
        sideSpin.setSelection(0);
    }


    private void setUpAmount() {
        popupView.findViewById(R.id.plusml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mls.setText(String.valueOf(Integer.parseInt(mls.getText().toString()) + 1));
            }
        });
        popupView.findViewById(R.id.minusml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(mls.getText().toString()) > 0)
                    mls.setText(String.valueOf(Integer.parseInt(mls.getText().toString()) - 1));
                else
                    Toast.makeText(context, "Amount can't be lower than 0.", Toast.LENGTH_LONG).show();
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
