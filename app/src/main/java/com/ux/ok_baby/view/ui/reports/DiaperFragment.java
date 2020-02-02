package com.ux.ok_baby.view.ui.reports;


import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.DiaperEntry;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpDiaper;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lecho.lib.hellocharts.formatter.ValueFormatterHelper;
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

        mTableLayout = tableView.findViewById(R.id.tableReportLayout);
        mGraphsLayout = graphView.findViewById(R.id.graphsLayout);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ReportPagerAdapter(tableView, graphView));
    }

    private void setUpReportTable() {
        entriesViewModel.getDiaperEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (reportEntries != null && reportEntries.size() > 0) {
                    // todo: remove sort from here- maybe in viewmodel when getting entries
                    reportEntries.sort(new Comparator<ReportEntry>() {
                        @Override
                        public int compare(ReportEntry o1, ReportEntry o2) {
                            DiaperEntry s1 = (DiaperEntry) o1;
                            DiaperEntry s2 = (DiaperEntry) o2;

                            // handle title row
                            if (s1.getDate().equals("date")) {
                                return -1;
                            } else if (s2.getDate().equals("date")) {
                                return 1;
                            }

                            return s1.getDate().compareTo(s2.getDate());
                        }
                    });

                    // todo: temp
                    ReportEntry titleEntry = (ReportEntry) reportEntries.get(0);
                    if (!titleEntry.getDataByField(0).equals("date")) {
                        reportEntries.add(0, new DiaperEntry("date", "time", "type", "texture", "color"));
                    }

                    mTableAdapter = new ReportTableAdapter(getContext(), reportEntries);
                    mTableLayout.setAdapter(mTableAdapter);
                    mTableAdapter.notifyDataSetChanged();
                    setUpGraphs(reportEntries);
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

        boolean hasAxes = true;
        boolean hasAxesNames = true;
        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
//                axisX.setName("Axis X");
                axisY.setName("Number of diapers");
            }
            axisX.setValues(axisXValues);

            final ValueFormatterHelper helper = new ValueFormatterHelper();
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setStacked(false);
        chart.setColumnChartData(data);
    }

    private List<Column> generateDataForGraph(List<ReportEntry> reportEntries, List<AxisValue> axisValues) {
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        int i = 1;
        DiaperEntry entry;
        int typesOfDiaperEntries = 2;

        int numOfDates = 0;
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> numOfPooEntriesPerDay = new ArrayList<>();
        ArrayList<Integer> numOfPeeEntriesPerDay = new ArrayList<>();

        // iterate over all entries
        while (i < reportEntries.size()) {
            entry = (DiaperEntry) reportEntries.get(i);
            String currentDate = entry.getDate();

            // iterate over all entries for a certain date
            int pooEntriesPerDay = 0;
            int peeEntriesPerDay = 0;
            int j = i;
            while (entry.getDate().equals(currentDate) && j < reportEntries.size()) {
                if (entry.getType().equals(POO)) {
                    pooEntriesPerDay++;
                } else if (entry.getType().equals(PEE)) {
                    peeEntriesPerDay++;
                }
                j++;
                if (j < reportEntries.size()) {
                    entry = (DiaperEntry) reportEntries.get(j);
                }
            }

            numOfPooEntriesPerDay.add(pooEntriesPerDay);
            numOfPeeEntriesPerDay.add(peeEntriesPerDay);

            // generate lavels
            AxisValue axisValue = new AxisValue(dates.size());
            axisValue.setLabel(currentDate);
            axisValues.add(axisValue);

            dates.add(currentDate);

            if (j != i) {
                i = j;
            } else {
                i++;
            }
        }

        ArrayList<Integer>[] diaperEntries = new ArrayList[]{numOfPooEntriesPerDay, numOfPeeEntriesPerDay};

        // iterate over dates (columns)
        int[] colors = {ContextCompat.getColor(getContext(), R.color.colorPrimaryDark),
                ContextCompat.getColor(getContext(), R.color.colorPrimary)};
        String[] labels = {POO, PEE};
        for (int k = 0; k < dates.size(); ++k) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < typesOfDiaperEntries; ++j) {
                SubcolumnValue value = new SubcolumnValue(diaperEntries[j].get(k), colors[j]);
                value.setLabel((Integer) Math.round(value.getValue()) + "");
                values.add(value);
            }

            Column column = new Column(values);

//            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
        }

        return columns;
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
