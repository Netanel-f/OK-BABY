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
import com.ux.ok_baby.model.DiaperEntry;
import com.ux.ok_baby.model.EntryDataComparator;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpDiaper;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

import static com.ux.ok_baby.utils.Constants.*;


public class DiaperFragment extends Fragment {
    private View view;
    private String babyID;
    private LinearLayout mGraphsLayout;
    private ReportTableAdapter mTableAdapter;
    private AdaptiveTableLayout mTableLayout;
    private EntriesViewModel entriesViewModel;
    private ConstraintLayout mEmptyTableError;
    private ImageView graphsButton, tableButton;

    public DiaperFragment(String babyID) {
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
        view = inflater.inflate(R.layout.fragment_diaper, container, false);
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);

        View tableView = inflater.inflate(R.layout.report_table_view, container, false);
        View graphView = inflater.inflate(R.layout.report_graph_view, container, false);

        graphsButton = view.findViewById(R.id.graphsButton);
        tableButton = view.findViewById(R.id.tableButton);

        mTableLayout = tableView.findViewById(R.id.tableReportLayout);
        mGraphsLayout = graphView.findViewById(R.id.graphsLayout);
        mEmptyTableError = tableView.findViewById(R.id.emptyTableError);

        // set up ViewPager for switching table/graph view
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ReportPagerAdapter(tableView, graphView));
        setUpButtons(viewPager);
    }

    /**
     * Set up the graph/table buttons.
     *
     * @param viewPager - ViewPager for switching table/graph view.
     */
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

    /**
     * Toggle graph and table.
     *
     * @param destination - graph or table.
     * @param viewPager   - ViewPager for switching table/graph view.
     * @param graphsBtn   - button for graphs.
     * @param tableBtn    - button for table.
     */
    private void toggleGraphAndTable(int destination, ViewPager viewPager, ImageView graphsBtn, ImageView tableBtn) {
        viewPager.setCurrentItem(destination, true);
        toggleButtonVisibility(graphsBtn);
        toggleButtonVisibility(tableBtn);
    }

    /**
     * Toggle button visibility.
     *
     * @param button - button to toggle.
     */
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
            ReportEntry titleEntry = reportEntries.get(0);
            if (!titleEntry.getDataByField(0).equals("date")) {
                reportEntries.add(0, new DiaperEntry("date", "time", "type", "texture"));
            }
        } else {
            mEmptyTableError.setVisibility(View.VISIBLE);
            reportEntries = new ArrayList<>();
            reportEntries.add(0, new DiaperEntry("date", "time", "type", "texture"));
        }
    }

    /**
     * Sets up the table.
     */
    private void setUpReportTable() {
        final Context context = getContext();
        entriesViewModel.getDiaperEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (entriesViewModel.isFirstTimeDiaper()) {
                    updateReportEntries(reportEntries);
                    if (!reportEntries.isEmpty()) {
                        mTableAdapter = new ReportTableAdapter(context, reportEntries);
                        mTableLayout.setAdapter(mTableAdapter);
                        mTableAdapter.notifyDataSetChanged();
                        setUpGraphs(reportEntries);
                        entriesViewModel.setIsFirstTimeDiaper(false);
                    }
                } else {
                    entriesViewModel.setIsFirstTimeDiaper(true);
                }
            }
        });
    }

    /**
     * Sets up the graphs.
     */
    private void setUpGraphs(List<ReportEntry> reportEntries) {
        ColumnChartView chart = new ColumnChartView(view.getContext());
        mGraphsLayout.addView(chart);

        List<AxisValue> axisXValues = new ArrayList<>();
        List<Column> columns = generateDataForGraph(reportEntries, axisXValues);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisY.setName("Number of diapers");
        axisX.setValues(axisXValues);

        ColumnChartData data = new ColumnChartData(columns);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setStacked(false);
        chart.setColumnChartData(data);
    }

    /**
     * Generates data for graph.
     */
    private List<Column> generateDataForGraph(List<ReportEntry> reportEntries, List<AxisValue> axisValues) {
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer>[] diaperEntries = getTypePerDay(reportEntries, axisValues, dates);
        return getColumns(diaperEntries, dates);
    }

    /**
     * Get columns for the graph.
     */
    private List<Column> getColumns(ArrayList<Integer>[] diaperEntries, ArrayList<String> dates) {
        int[] colors = {ContextCompat.getColor(getContext(), R.color.colorPrimaryDark),
                ContextCompat.getColor(getContext(), R.color.colorPrimary)};
        List<Column> columns = new ArrayList<Column>();

        for (int k = 0; k < dates.size(); ++k) {
            // count diapers per day
            List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < NUM_OF_TYPES; ++j) {
                // count pee/poo
                SubcolumnValue value = new SubcolumnValue(diaperEntries[j].get(k), colors[j]);
                value.setLabel((Integer) Math.round(value.getValue()) + "");
                values.add(value);
            }
            Column column = new Column(values);
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
        }
        return columns;
    }

    /**
     * Get number of pee/poo diapers by date.
     */
    private int getNumOfTypeByDate(ArrayList<Integer> numOfPooEntriesPerDay, ArrayList<Integer> numOfPeeEntriesPerDay,
                                   List<ReportEntry> reportEntries, int i) {
        DiaperEntry entry = (DiaperEntry) reportEntries.get(i);
        String currentDate = entry.getDate();

        int pooEntriesPerDay = 0, peeEntriesPerDay = 0, j = i;
        while (entry.getDate().equals(currentDate) && j < reportEntries.size()) {
            if (entry.getType().equals(POO))
                pooEntriesPerDay++;
            else if (entry.getType().equals(PEE))
                peeEntriesPerDay++;

            j++;

            if (j < reportEntries.size())
                entry = (DiaperEntry) reportEntries.get(j);
        }

        numOfPooEntriesPerDay.add(pooEntriesPerDay);
        numOfPeeEntriesPerDay.add(peeEntriesPerDay);

        return j;
    }

    /**
     * Get type of pee/poo diapers by date.
     */
    private ArrayList<Integer>[] getTypePerDay(List<ReportEntry> reportEntries,
                                               List<AxisValue> axisValues, ArrayList<String> dates) {
        ArrayList<Integer> numOfPooEntriesPerDay = new ArrayList<>();
        ArrayList<Integer> numOfPeeEntriesPerDay = new ArrayList<>();

        int i = 1;
        while (i < reportEntries.size()) {
            DiaperEntry entry = (DiaperEntry) reportEntries.get(i);
            String currentDate = entry.getDate();

            int j = getNumOfTypeByDate(numOfPooEntriesPerDay, numOfPeeEntriesPerDay, reportEntries, i);

            // generate levels
            AxisValue axisValue = new AxisValue(dates.size());
            axisValue.setLabel(currentDate);
            axisValues.add(axisValue);
            dates.add(currentDate);

            if (j != i)
                i = j;
            else
                i++;
        }
        return new ArrayList[]{numOfPooEntriesPerDay, numOfPeeEntriesPerDay};
    }

    /**
     * Sets pop up when FAB is clicked.
     */
    private void onAddClickListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpDiaper popUpClass = new PopUpDiaper(getActivity(), babyID, entriesViewModel);
                popUpClass.showPopupWindow(view);
            }
        });
    }
}
