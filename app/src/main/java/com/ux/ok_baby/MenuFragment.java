package com.ux.ok_baby;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static com.ux.ok_baby.Constants.*;

public class MenuFragment extends Fragment implements View.OnClickListener {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.menu_layout, container, false);

        view.findViewById(R.id.diaperButton).setOnClickListener(this);
        view.findViewById(R.id.foodButton).setOnClickListener(this);
        view.findViewById(R.id.otherButton).setOnClickListener(this);
        view.findViewById(R.id.sleepButton).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //todo;  delete maybe?
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sleepButton:
                startNewFragment(SLEEP);
                break;
            case R.id.diaperButton:
                startNewFragment(DIAPER);
                break;
            case R.id.foodButton:
                startNewFragment(FOOD);
                break;
            case R.id.otherButton:
                startNewFragment(OTHER);
                break;
        }
    }

    @SuppressLint("ResourceType")
    private void startNewFragment(String reportType) {
        ReportsHolderFragment reportsHolderFragment = new ReportsHolderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(REPORT_TYPE, reportType);
        reportsHolderFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, reportsHolderFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}