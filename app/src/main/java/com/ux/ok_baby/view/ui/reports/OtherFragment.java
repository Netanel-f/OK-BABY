package com.ux.ok_baby.view.ui.reports;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ux.ok_baby.R;


/**
 * Contains the other report.
 */
public class OtherFragment extends Fragment {


    public OtherFragment(String babyID) {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other, container, false);
    }

}
