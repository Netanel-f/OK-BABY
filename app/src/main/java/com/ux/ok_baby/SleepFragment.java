package com.ux.ok_baby;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.google.firebase.firestore.CollectionReference;


/**
 * Contains the sleep report.
 */
public class SleepFragment extends Fragment {
    private AdaptiveTableLayout mTableLayout;
    private ReportTableAdapter mTableAdapter;

    public SleepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);

        // bind
        mTableLayout = (AdaptiveTableLayout) view.findViewById(R.id.tableReportLayout);

        CollectionReference dataSource = null; // todo - query from firebase
        mTableAdapter = new ReportTableAdapter(getContext(), dataSource);
        mTableLayout.setAdapter(mTableAdapter);
        mTableLayout.setHeaderFixed(true);
        mTableLayout.setSolidRowHeader(true);
        mTableAdapter.notifyDataSetChanged();

        return view;
    }

}
