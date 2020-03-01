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


/**
 * Contains the diaper report.
 */
public class DiaperFragment extends Fragment {
    private final String TAG = "DiaperFragment";
    private EntriesViewModel entriesViewModel;
    private AdaptiveTableLayout mTableLayout;
    private LinearLayout mGraphsLayout;
    private ReportTableAdapter mTableAdapter;
    private ConstraintLayout mEmptyTableError;
    private String babyID;
    private View view;

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
        final ImageView graphsBtn = view.findViewById(R.id.graphs_button);
        final ImageView tableBtn = view.findViewById(R.id.table_button);

        mTableLayout = tableView.findViewById(R.id.tableReportLayout);
        mGraphsLayout = graphView.findViewById(R.id.graphsLayout);
        mEmptyTableError = tableView.findViewById(R.id.empty_table_error);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ReportPagerAdapter(tableView, graphView));
        setUpButtons(viewPager, graphsBtn, tableBtn);
    }

    private void setUpButtons(final ViewPager viewPager, final ImageView graphsBtn, final ImageView tableBtn) {
        graphsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGraphAndTable(1, viewPager, graphsBtn, tableBtn);
            }
        });
        tableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGraphAndTable(0, viewPager, graphsBtn, tableBtn);
            }
        });
    }

    private void toggleGraphAndTable(int destination, ViewPager viewPager, ImageView graphsBtn, ImageView tableBtn){
        viewPager.setCurrentItem(destination, true);
        toggleButtonVisibility(graphsBtn);
        toggleButtonVisibility(tableBtn);
    }

    private void toggleButtonVisibility(ImageView button){
        if (button.getVisibility() == View.VISIBLE){
            button.setVisibility(View.INVISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }

    private void setUpReportTable() {
        final Context context = getContext();
        entriesViewModel.getDiaperEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (entriesViewModel.isFirstTimeDiaper()) {
                    if (reportEntries != null && reportEntries.size() > 0) {
                        mEmptyTableError.setVisibility(View.GONE);
                        reportEntries.sort(new EntryDataComparator());
                        ReportEntry titleEntry = (ReportEntry) reportEntries.get(0);
                        if (!titleEntry.getDataByField(0).equals("date")) {
                            reportEntries.add(0, new DiaperEntry("date", "time", "type", "texture"));
                        }
                    } else {
                        mEmptyTableError.setVisibility(View.VISIBLE);
                        reportEntries = new ArrayList<>();
                        reportEntries.add(0, new DiaperEntry("date", "time", "type", "texture"));
                    }

                    mTableAdapter = new ReportTableAdapter(context, reportEntries);
                    mTableLayout.setAdapter(mTableAdapter);
                    mTableAdapter.notifyDataSetChanged();
                    setUpGraphs(reportEntries);
                    entriesViewModel.setIsFirstTimeDiaper(false);
                } else {
                    entriesViewModel.setIsFirstTimeDiaper(true);
                }
            }
        });
    }

    private void setUpGraphs(List<ReportEntry> reportEntries) {
        ColumnChartView chart = new ColumnChartView(view.getContext());
        mGraphsLayout.addView(chart);
        ColumnChartData data;

        List<AxisValue> axisXValues = new ArrayList<>();
        List<Column> columns = generateDataForGraph(reportEntries, axisXValues);

        data = new ColumnChartData(columns);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisY.setName("Number of diapers");
        axisX.setValues(axisXValues);

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setStacked(false);
        chart.setColumnChartData(data);
    }

    private List<Column> generateDataForGraph(List<ReportEntry> reportEntries, List<AxisValue> axisValues) {
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer>[] diaperEntries = getTypePerDay(reportEntries, axisValues, dates);
        return getColumns(diaperEntries, dates);
    }

    private List<Column> getColumns(ArrayList<Integer>[] diaperEntries, ArrayList<String> dates) {
        int[] colors = {ContextCompat.getColor(getContext(), R.color.colorPrimaryDark),
                ContextCompat.getColor(getContext(), R.color.colorPrimary)};
        List<Column> columns = new ArrayList<Column>();

        for (int k = 0; k < dates.size(); ++k) {
            List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < NUM_OF_TYPES; ++j) {
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

    private ArrayList<Integer>[] getTypePerDay(List<ReportEntry> reportEntries, List<AxisValue> axisValues, ArrayList<String> dates) {
        ArrayList<Integer> numOfPooEntriesPerDay = new ArrayList<>();
        ArrayList<Integer> numOfPeeEntriesPerDay = new ArrayList<>();

        int i = 1;
        while (i < reportEntries.size()) {
            DiaperEntry entry = (DiaperEntry) reportEntries.get(i);
            String currentDate = entry.getDate();

            int j = getNumOfTypeByDate(numOfPooEntriesPerDay, numOfPeeEntriesPerDay, reportEntries, i);

            // generate lavels
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
