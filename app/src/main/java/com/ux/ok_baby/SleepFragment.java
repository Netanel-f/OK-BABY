package com.ux.ok_baby;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.google.firebase.firestore.CollectionReference;


/**
 * Contains the sleep report.
 */
public class SleepFragment extends Fragment {
    private AdaptiveTableLayout mTableLayout;
    private ReportTableAdapter mTableAdapter;
    private Button graphsBtn;
    private Button tableBtn;

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
        graphsBtn = (Button) view.findViewById(R.id.switch_to_graph_btn);
        tableBtn = (Button) view.findViewById(R.id.switch_to_table_btn);

        CollectionReference dataSource = null; // todo - query from firebase
        mTableAdapter = new ReportTableAdapter(getContext(), dataSource);
        mTableLayout.setAdapter(mTableAdapter);
        mTableLayout.setHeaderFixed(true);
        mTableLayout.setSolidRowHeader(false);
        mTableAdapter.notifyDataSetChanged();

        setUpGraphsBtn();

        return view;
    }

    private void setUpGraphsBtn(){
        graphsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphsBtn.setVisibility(View.GONE);
                tableBtn.setVisibility(View.VISIBLE);
                mTableLayout.setVisibility(View.GONE);
            }
        });
        tableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphsBtn.setVisibility(View.VISIBLE);
                tableBtn.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.VISIBLE);
            }
        });
    }

}
