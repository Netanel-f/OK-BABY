package com.ux.ok_baby.view.ui.reports;


import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import static com.ux.ok_baby.utils.Constants.*;

public class FoodFragment extends Fragment {
    private final String TAG = "FoodFragment";
    private View view;
    private String babyID;
    private LinearLayout mGraphsLayout;
    private ReportTableAdapter mTableAdapter;
    private AdaptiveTableLayout mTableLayout;
    private EntriesViewModel entriesViewModel;
    private ConstraintLayout mEmptyTableError;
    private ImageView graphsButton, tableButton;

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

        graphsButton = view.findViewById(R.id.graphsButton);
        tableButton = view.findViewById(R.id.tableButton);

        mTableLayout = tableView.findViewById(R.id.tableReportLayout);
        mGraphsLayout = graphView.findViewById(R.id.graphsLayout);
        mEmptyTableError = tableView.findViewById(R.id.emptyTableError);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ReportPagerAdapter(tableView, graphView));
        setUpButtons(viewPager);
    }

    private void setUpButtons(final ViewPager viewPager) {
        graphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGraphAndTable(1, viewPager, graphsButton, tableButton);
            }
        });
        tableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGraphAndTable(0, viewPager, graphsButton, tableButton);
            }
        });
    }

    private void toggleGraphAndTable(int destination, ViewPager viewPager, ImageView graphsBtn, ImageView tableBtn) {
        viewPager.setCurrentItem(destination, true);
        toggleButtonVisibility(graphsBtn);
        toggleButtonVisibility(tableBtn);
    }

    private void toggleButtonVisibility(ImageView button) {
        if (button.getVisibility() == View.VISIBLE) {
            button.setVisibility(View.INVISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }

    /**
     * If reportEntries is empty, add titles. otherwise, sort reportEntries and add titles if not exist.
     */
    private void updateReportEntries(List<ReportEntry> reportEntries) {
        if (reportEntries != null && reportEntries.size() > 0) {
            mEmptyTableError.setVisibility(View.GONE);
            reportEntries.sort(new EntryDataComparator());
            ReportEntry titleEntry = (ReportEntry) reportEntries.get(0);
            if (!titleEntry.getDataByField(0).equals("date")) {
                reportEntries.add(0, new FoodEntry("date", "start", "end", "type", "amount"));
            }
        } else {
            mEmptyTableError.setVisibility(View.VISIBLE);
            reportEntries = new ArrayList<>();
            reportEntries.add(0, new FoodEntry("date", "start", "end", "type", "amount"));
        }
    }

    private void setUpReportTable() {
        final Context context = getContext();
        entriesViewModel.getFoodEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (entriesViewModel.isFirstTimeFood()) {
                    updateReportEntries(reportEntries);
                    if (!reportEntries.isEmpty()) {
                        mTableAdapter = new ReportTableAdapter(context, reportEntries);
                        mTableLayout.setAdapter(mTableAdapter);
                        mTableAdapter.notifyDataSetChanged();
                        setUpGraphs(reportEntries);
                    }
                    entriesViewModel.setIsFirstTimeFood(false);
                } else {
                    entriesViewModel.setIsFirstTimeFood(true);
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
        int[] numOfEntries = getNumOfEntries(reportEntries);
        return getSlices(numOfEntries);
    }

    private List<SliceValue> getSlices(int[] numOfEntries) {
        List<SliceValue> values = new ArrayList<SliceValue>();
        int sum = numOfEntries[0] + numOfEntries[1];

        int[] colors = {ContextCompat.getColor(getContext(), R.color.colorPrimary),
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)};

        String[] labels = {"Bottle\n", "Breastfeeding\n"};

        for (int i = 0; i < NUM_OF_TYPES; ++i) {
            SliceValue sliceValue = new SliceValue((float) numOfEntries[i], colors[i % NUM_OF_TYPES]);
            sliceValue.setLabel(labels[i] + round((sliceValue.getValue() / sum) * 100) + "%");
            values.add(sliceValue);
        }
        return values;
    }

    private int[] getNumOfEntries(List<ReportEntry> reportEntries) {
        int BOTTLE = 0, BREASTFEEDING = 1;
        int[] numOfEntries = new int[NUM_OF_TYPES];
        for (int j = 1; j < reportEntries.size(); ++j) {
            FoodEntry foodEntry = (FoodEntry) reportEntries.get(j);
            if (foodEntry.getType().equals("Bottle"))
                numOfEntries[BOTTLE]++;
            else if (foodEntry.getType().equals("Breastfeeding"))
                numOfEntries[BREASTFEEDING]++;
        }
        return numOfEntries;
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
