package com.ux.ok_baby.view.ui.reports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.ux.ok_baby.utils.Constants.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ux.ok_baby.R;

public class ReportsHolderFragment extends Fragment {
    private String babyID;
    private int reportType = -1;
    private BottomNavigationView bottomNavigationView;

    public ReportsHolderFragment(String babyID) {
        this.babyID = babyID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_layout, container, false);
        reportType = getArguments().getInt(REPORT_TYPE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupNavView(view);
        setClickedItem();
    }

    /**
     * This method will set the clicked item
     */
    private void setClickedItem() {
        if (reportType != -1)
            bottomNavigationView.setSelectedItemId(reportType);
    }


    /**
     * this method will setup the navigation view.
     * @param view View to setup in
     */
    private void setupNavView(@NonNull View view) {
        bottomNavigationView = view.findViewById(R.id.homeNavBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_sleep:
                                startNewFragment(new SleepFragment(babyID));
                                reportType = R.id.action_sleep;
                                return true;
                            case R.id.action_food:
                                startNewFragment(new FoodFragment(babyID));
                                reportType = R.id.action_food;
                                return true;
                            case R.id.action_diaper:
                                startNewFragment(new DiaperFragment(babyID));
                                reportType = R.id.action_diaper;
                                return true;
                        }
                        return false;
                    }
                });
    }

    @SuppressLint("ResourceType")
    private void startNewFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer1, fragment).commit();
    }

    /**
     * This method will update the baby ID
     * @param babyID baby ID to update
     */
    public void updateBabyID(String babyID) {
        this.babyID = babyID;
        setClickedItem();
    }
}