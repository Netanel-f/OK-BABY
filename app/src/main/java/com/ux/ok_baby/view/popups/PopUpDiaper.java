package com.ux.ok_baby.view.popups;

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

import com.ux.ok_baby.model.DiaperEntry;
import com.ux.ok_baby.R;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import static com.ux.ok_baby.utils.Constants.*;

public class PopUpDiaper {
    private String babyID;
    private View popupView;
    private Context context;
    private Button typeB, textureB;
    private DiaperEntry diaperEntry;
    private PopupWindow popupWindow;
    private DateTimePicker dateTimePicker;
    private EntriesViewModel entriesViewModel;
    private EditText dateET, timeET;


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
        dateET = popupView.findViewById(R.id.date);
        timeET = popupView.findViewById(R.id.time);
        typeB = popupView.findViewById(R.id.type);
        textureB = popupView.findViewById(R.id.texture);
        // TODO: 1/12/2020 update texture and color.
        setUpDate();
        setUpTime();
        onTypeClick();
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
        diaperEntry.setDate(dateET.getText().toString());
        diaperEntry.setTime(timeET.getText().toString());
        diaperEntry.setType(typeB.getText().toString());
        if (diaperEntry.getType().equals(POO)) {
            diaperEntry.setTexture(textureB.getText().toString());
            diaperEntry.setColor("");//TODO update color
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
                } else {
                    Toast.makeText(context, "One or more fields are empty", Toast.LENGTH_LONG).show();
                }
            }
        });
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
                            typeB.setText(POO);
                        } else {
                            popupView.findViewById(R.id.textureLayout).setVisibility(View.GONE);
                            popupView.findViewById(R.id.colorLayout).setVisibility(View.GONE);
                            typeB.setText(PEE);
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
            }
        });
    }

    private void setUpTime() {
        timeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.timePicker(timeET);
            }
        });
    }
}

