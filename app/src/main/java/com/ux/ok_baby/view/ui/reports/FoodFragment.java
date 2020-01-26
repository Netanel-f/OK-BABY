package com.ux.ok_baby.view.ui.reports;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.google.firebase.firestore.CollectionReference;
import com.ux.ok_baby.R;
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpFood;
import com.ux.ok_baby.viewmodel.EntriesViewModel;


/**
 * Contains the food report.
 */
public class FoodFragment extends Fragment {
    private EntriesViewModel entriesViewModel;
    private AdaptiveTableLayout mTableLayout;
    private ReportTableAdapter mTableAdapter;
    private Button graphsBtn, tableBtn;
    private String babyID;

    public FoodFragment(String babyID) {
        this.babyID = babyID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);

        // bind
        mTableLayout = view.findViewById(R.id.tableReportLayout);
        graphsBtn = view.findViewById(R.id.switch_to_graph_btn);
        tableBtn = view.findViewById(R.id.switch_to_table_btn);

        setUpGraphsBtn();
        onAddClickListener(view.findViewById(R.id.addReport));
        return view;
    }

    private void setUpReportTable() {
//        CollectionReference dataSource = null; // todo - query from firebase
//        mTableAdapter = new ReportTableAdapter(getContext(), dataSource);
//        mTableLayout.setAdapter(mTableAdapter);
//        mTableLayout.setHeaderFixed(true);
//        mTableLayout.setSolidRowHeader(false);
//        mTableAdapter.notifyDataSetChanged();
    }

    private void setUpGraphsBtn() {
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

    private void onAddClickListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpFood popUpClass = new PopUpFood(getActivity(), babyID, entriesViewModel);
                popUpClass.showPopupWindow(view);
            }
        });
    }
}
