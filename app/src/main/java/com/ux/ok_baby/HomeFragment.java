package com.ux.ok_baby;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * First screen when loading the app (after sign in).
 * Contains buttons to navigate to other screens.
 */
public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    public void setUpOptions(View view) {
        switch (view.getId()){
            case R.id.sleepButton:
                // TODO: 12/30/2019 start an sleep fragment
            case R.id.diaperButton:
                // TODO: 12/30/2019 start an diaper fragment
            case R.id.foodButton:
                // TODO: 12/30/2019 start an food fragment
            case R.id.otherButton:
                // TODO: 12/30/2019 other?
        }
    }
    public void setUpBabyDetails(String name, String age){

    }

    
}
