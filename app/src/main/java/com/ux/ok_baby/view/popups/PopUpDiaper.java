package com.ux.ok_baby.view.popups;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ux.ok_baby.model.DiaperEntry;
import com.ux.ok_baby.R;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.HashMap;
import java.util.Map;

import static com.ux.ok_baby.utils.Constants.*;

public class PopUpDiaper {
    private String babyID;
    private View popupView;
    private Context context;
    private Spinner typeSpin, textureSpin;
    private DiaperEntry diaperEntry;
    private PopupWindow popupWindow;
    private DateTimePicker dateTimePicker;
    private EntriesViewModel entriesViewModel;
    private TextView dateTV, timeTV;
    private HashMap<Button, Integer> colorBtns = new HashMap<>();

    public PopUpDiaper(Context context, String babyID, EntriesViewModel entriesViewModel) {
        this.context = context;
        this.babyID = babyID;
        this.entriesViewModel = entriesViewModel;
    }

    public void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_window_diaper, null);
        popupWindow = setupPopup(view, popupView);
        diaperEntry = new DiaperEntry();
        dateTimePicker = new DateTimePicker(context);

        setUpEntry();
        setUpAddButton();
        setUpExit();
    }

    private void setUpEntry() {
        dateTV = popupView.findViewById(R.id.date);
        timeTV = popupView.findViewById(R.id.time);
        typeSpin = popupView.findViewById(R.id.type);
        textureSpin = popupView.findViewById(R.id.texture);

        setUpDate();
        setUpTime();
        setUpType();
        setUpTexture();
        setUpColors();
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

    private void updateDiaperEntryObject() {
        diaperEntry.setDate(dateTV.getText().toString());
        diaperEntry.setTime(timeTV.getText().toString());
        diaperEntry.setType(typeSpin.getSelectedItem().toString());
        if (diaperEntry.getType().equals(POO)) {
            diaperEntry.setTexture(textureSpin.getSelectedItem().toString());
            diaperEntry.setColor("");
        } else {
            diaperEntry.setTexture("");
            diaperEntry.setColor("");
        }
    }


    private void setUpAddButton() {
        popupView.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDiaperEntryObject();
                if (diaperEntry.isValidEntry()) {
                    entriesViewModel.addDiaperEntry(babyID, diaperEntry);
                    popupWindow.dismiss();
                } else {
                    Toast.makeText(context, "One or more fields are empty", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setUpColors() {
        Button redBtn = popupView.findViewById(R.id.redColor);
        Button blackBtn = popupView.findViewById(R.id.blackColor);
        Button brownBtn = popupView.findViewById(R.id.brownColor);

        colorBtns.put(redBtn, R.color.red);
        colorBtns.put(blackBtn, R.color.black);
        colorBtns.put(brownBtn, R.color.brown);

        onColorClickListener(redBtn);
        onColorClickListener(blackBtn);
        onColorClickListener(brownBtn);

        brownBtn.performClick();
    }

    private void onColorClickListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFrameColor((LayerDrawable) view.getBackground(), R.color.colorAccent);
                for (Map.Entry entry : colorBtns.entrySet()) {
                    Button curBtn = (Button) entry.getKey();
                    if (curBtn.getId() != view.getId()) {
                        changeFrameColor((LayerDrawable) curBtn.getBackground(), (int) entry.getValue());
                    } else {
                        diaperEntry.setColor(POO_COLORS.get(entry.getValue()));
                    }
                }
            }
        });
    }

    private void changeFrameColor(LayerDrawable layerDrawable, int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable
                .findDrawableByLayerId(R.id.frame);
        gradientDrawable.setColor(context.getColor(color));
    }

    private void setUpType() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(context, R.array.diaper_type,
                        android.R.layout.simple_spinner_dropdown_item);
        typeSpin.setAdapter(adapter);
        typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String type = (String) adapterView.getItemAtPosition(i);
                if (type.equals(POO)) {
                    popupView.findViewById(R.id.textureLayout).setVisibility(View.VISIBLE);
                    popupView.findViewById(R.id.colorLayout).setVisibility(View.VISIBLE);
                } else {
                    popupView.findViewById(R.id.textureLayout).setVisibility(View.GONE);
                    popupView.findViewById(R.id.colorLayout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        typeSpin.setSelection(0);
    }

    private void setUpTexture() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(context, R.array.diaper_texture,
                        android.R.layout.simple_spinner_dropdown_item);
        textureSpin.setAdapter(adapter);
        textureSpin.setSelection(0);
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

    private void setUpTime() {
        timeTV.setText(dateTimePicker.getCurTime());
        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.timePicker(timeTV);
            }
        });
    }
}

