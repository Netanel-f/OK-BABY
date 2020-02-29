package com.ux.ok_baby.view.ui.reports;


import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.EntryDataComparator;
import com.ux.ok_baby.model.FoodEntry;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpFood;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static java.lang.Math.round;


/**
 * Contains the food report.
 */
public class FoodFragment extends Fragment {
    private final String TAG = "FoodFragment";
    private EntriesViewModel entriesViewModel;
    private AdaptiveTableLayout mTableLayout;
    private LinearLayout mGraphsLayout;
    private ReportTableAdapter mTableAdapter;
    private ConstraintLayout mEmptyTableError;
    private String babyID;
    private View view;

    public FoodFragment(String babyID) {
        this.babyID = babyID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpView(inflater, container);
        setUpReportTable();
        onAddClickListener(view.findViewById(R.id.addReport));
        return view;
    }

    private void setUpView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_food, container, false);
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);

        View tableView = inflater.inflate(R.layout.report_table_view, container, false);
        View graphView = inflater.inflate(R.layout.report_graph_view, container, false);

        mTableLayout = tableView.findViewById(R.id.tableReportLayout);
        mGraphsLayout = graphView.findViewById(R.id.graphsLayout);
        mEmptyTableError = tableView.findViewById(R.id.empty_table_error);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ReportPagerAdapter(tableView, graphView));
    }

    private void setUpReportTable() {
        final Context context = getContext();
        entriesViewModel.getFoodEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (reportEntries != null && reportEntries.size() > 0) {
                    mEmptyTableError.setVisibility(View.GONE);
                    reportEntries.sort(new EntryDataComparator());
                    ReportEntry titleEntry = (ReportEntry) reportEntries.get(0);
                    if (!titleEntry.getDataByField(0).equals("date")) {
                        reportEntries.add(0, new FoodEntry("date", "start", "end", "type", "side", "amount"));
                    }
                } else {
                    // empty table
                    mEmptyTableError.setVisibility(View.VISIBLE);
                    reportEntries = new ArrayList<>();
                    reportEntries.add(0, new FoodEntry("date", "start", "end", "type", "side", "amount"));

                }

                if (!reportEntries.isEmpty()) {
                    mTableAdapter = new ReportTableAdapter(context, reportEntries);
                    mTableLayout.setAdapter(mTableAdapter);
                    mTableAdapter.notifyDataSetChanged();
                    setUpGraphs(reportEntries);
                } else{
                    Log.d(TAG, "onChanged: empty entries, no title.");
                }
            }
        });
    }


    private void setUpGraphs(List<ReportEntry> reportEntries) {
        PieChartView chart = new PieChartView(view.getContext());
        mGraphsLayout.addView(chart);
        PieChartData data;

        List<SliceValue> values = generateDataForGraph(reportEntries);
        data = new PieChartData(values);
        data.setHasLabels(true);
        data.setHasCenterCircle(true);
        data.setCenterCircleScale(0.40f);
        chart.setPieChartData(data);
    }

    private List<SliceValue> generateDataForGraph(List<ReportEntry> reportEntries) {
        int sum, numValues = 2, BOTTLE = 0, BREASTFEEDING = 1;
        List<SliceValue> values = new ArrayList<SliceValue>();
        int[] numOfEntries = new int[numValues];

        for (int j = 1; j < reportEntries.size(); ++j){
//        for (ReportEntry entry : reportEntries) {

//            FoodEntry foodEntry = (FoodEntry) entry;
            FoodEntry foodEntry = (FoodEntry) reportEntries.get(j);
            if (foodEntry.getType().equals("Bottle")) {
                numOfEntries[BOTTLE]++;
            } else if (foodEntry.getType().equals("Breastfeeding")) {
                numOfEntries[BREASTFEEDING]++;
            }
        }
        sum = numOfEntries[0] + numOfEntries[1];

        int[] colors = {ContextCompat.getColor(getContext(), R.color.colorPrimary),
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)};

        String[] labels = {"Bottle\n", "Breastfeeding\n"};

        for (int i = 1; i < numValues; ++i) {
            SliceValue sliceValue = new SliceValue((float) numOfEntries[i], colors[i % numValues]);
            sliceValue.setLabel(labels[i] + round((sliceValue.getValue() / sum) * 100) + "%");
            values.add(sliceValue);
        }

        return values;
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
