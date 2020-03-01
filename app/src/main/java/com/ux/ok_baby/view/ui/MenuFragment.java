package com.ux.ok_baby.view.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ux.ok_baby.R;
import com.ux.ok_baby.view.ui.reports.ReportsHolderFragment;

import static com.ux.ok_baby.utils.Constants.*;

public class MenuFragment extends Fragment implements View.OnClickListener {
    private View view;
    private String babyID;
    private ReportsHolderFragment reportsHolderFragment;


    public MenuFragment(String babyID) {
        this.babyID = babyID;
    }

    public void updateBabyID(String babyID) {
        this.babyID = babyID;
        if (reportsHolderFragment != null)
            reportsHolderFragment.updateBabyID(babyID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.menu_layout, container, false);
        setUpOptions();

        return view;
    }

    private void setUpOptions() {
        view.findViewById(R.id.diaperButton).setOnClickListener(this);
        view.findViewById(R.id.foodButton).setOnClickListener(this);
        view.findViewById(R.id.sleepButton).setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sleepButton:
                startNewFragment(R.id.action_sleep);
                break;
            case R.id.foodButton:
                startNewFragment(R.id.action_food);
                break;
            case R.id.diaperButton:
                startNewFragment(R.id.action_diaper);
                break;
        }
    }

    @SuppressLint("ResourceType")
    private void startNewFragment(int reportType) {
        reportsHolderFragment = new ReportsHolderFragment(babyID);
        Bundle bundle = new Bundle();
        bundle.putInt(REPORT_TYPE, reportType);
        reportsHolderFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, reportsHolderFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
