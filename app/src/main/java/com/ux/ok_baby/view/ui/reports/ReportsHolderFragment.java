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
import androidx.lifecycle.ViewModelProvider;

import static com.ux.ok_baby.utils.Constants.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ux.ok_baby.R;
import com.ux.ok_baby.viewmodel.EntriesViewModel;


public class ReportsHolderFragment extends Fragment {

    private int reportType = -1;
    private String babyID;
    private BottomNavigationView bottomNavigationView;
    private EntriesViewModel entriesViewModel;
    DiaperFragment diaperFragment;

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
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpNavView(view);
        seClickedItem();
    }

    private void seClickedItem() {
        if (reportType != -1)
            bottomNavigationView.setSelectedItemId(reportType);
    }

    private void setUpNavView(@NonNull View view) {
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
                                diaperFragment = new DiaperFragment(babyID);
                                startNewFragment(diaperFragment);
                                reportType = R.id.action_diaper;
                                return true;
                        }
                        return false;
                    }
                });
    }

    @SuppressLint("ResourceType")
    private void startNewFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container1, fragment).commit();

    }

    public void updateBabyID(String babyID) {
        this.babyID = babyID;
        seClickedItem();
    }
}