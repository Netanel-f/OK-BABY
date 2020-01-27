package com.ux.ok_baby.view.ui.reports;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.FoodEntry;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpFood;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.Comparator;
import java.util.List;


/**
 * Contains the food report.
 */
public class FoodFragment extends Fragment {
    private EntriesViewModel entriesViewModel;
    private AdaptiveTableLayout mTableLayout;
    private LinearLayout mGraphsLayout;
    private ReportTableAdapter mTableAdapter;
    private Button graphsBtn, tableBtn;
    private String babyID;
//    date, startTime, endTime, type, side, amount

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
        mTableLayout = (AdaptiveTableLayout) view.findViewById(R.id.foodTableReportLayout);
        mGraphsLayout = (LinearLayout) view.findViewById(R.id.foodGraphsLayout);

        // todo: move
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                view.findViewById(R.id.foodBottomNavBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_table:
                                mTableLayout.setVisibility(View.VISIBLE);
                                mGraphsLayout.setVisibility(View.GONE);
                                break;
                            case R.id.action_chart:
                                mTableLayout.setVisibility(View.GONE);
                                mGraphsLayout.setVisibility(View.VISIBLE);
                                break;
                        }
                        return false;
                    }
                });


        setUpReportTable(babyID);
//        setUpGraphsBtn();
        onAddClickListener(view.findViewById(R.id.addReport));
        return view;
    }

    private void setUpReportTable(String babyID) {
        entriesViewModel.getFoodEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (reportEntries != null && reportEntries.size() > 0) {
                    // todo: remove sort from here- maybe in viewmodel when getting entries
                    reportEntries.sort(new Comparator<ReportEntry>() {
                        @Override
                        public int compare(ReportEntry o1, ReportEntry o2) {
                            FoodEntry s1 = (FoodEntry) o1;
                            FoodEntry s2 = (FoodEntry) o2;

                            // handle title row
                            if (s1.getDate().equals("date")){
                                return -1;
                            }
                            else if (s2.getDate().equals("date")){
                                return 1;
                            }

                            return s1.getDate().compareTo(s2.getDate());
                        }
                    });

                    // todo: temp
                    ReportEntry titleEntry = (ReportEntry) reportEntries.get(0);
                    if (!titleEntry.getDataByField(0).equals("date")) {
                        reportEntries.add(0, new FoodEntry("date", "start", "end", "type", "side", "amount"));
                    }

                    mTableAdapter = new ReportTableAdapter(getContext(), reportEntries);
                    mTableLayout.setAdapter(mTableAdapter);
                    mTableLayout.setHeaderFixed(true);
                    mTableLayout.setSolidRowHeader(false);
                    mTableAdapter.notifyDataSetChanged();
                    setUpGraphs(reportEntries);
                }
            }
        });
    }


    private void setUpGraphs(List<ReportEntry> reportEntries) {

    }

//    private void setUpGraphsBtn() {
//        graphsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                graphsBtn.setVisibility(View.GONE);
//                tableBtn.setVisibility(View.VISIBLE);
//                mTableLayout.setVisibility(View.GONE);
//            }
//        });
//        tableBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                graphsBtn.setVisibility(View.VISIBLE);
//                tableBtn.setVisibility(View.GONE);
//                mTableLayout.setVisibility(View.VISIBLE);
//            }
//        });
//    }

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
