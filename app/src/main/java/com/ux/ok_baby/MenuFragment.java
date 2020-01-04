package com.ux.ok_baby;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MenuFragment extends Fragment implements View.OnClickListener {
    View view;

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
        // TODO: 1/4/2020 click stuff

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sleepButton:
                startNewFragment(new SleepFragment(), R.layout.fragment_sleep);
                break;
            case R.id.diaperButton:
                // TODO: 1/4/2020  
//                startNewFragment(new DiaperFragment(),R.layout.fragment_food);
                break;
            case R.id.foodButton:
                startNewFragment(new FoodFragment(), R.layout.fragment_food);
                break;
            case R.id.otherButton:
                // TODO: 12/30/2019 other?
        }
    }

    private void startNewFragment(Fragment fragment, int layout) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
