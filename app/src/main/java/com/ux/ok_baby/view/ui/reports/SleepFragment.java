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
import android.widget.Toast;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.EntryDataComparator;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.model.SleepEntry;
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpSleep;
import com.ux.ok_baby.view.ui.HomeFragment;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Contains the sleep report.
 */
public class SleepFragment extends Fragment {
    private final String TAG = "SleepFragment";
    private EntriesViewModel entriesViewModel;
    private AdaptiveTableLayout mTableLayout;
    private ReportTableAdapter mTableAdapter;
    private LinearLayout mGraphsLayout;
    private ConstraintLayout mEmptyTableError;
    private String babyID;
    private View view;

    public SleepFragment(String babyID) {
        this.babyID = babyID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpView(inflater, container);
        onAddClickListener(view.findViewById(R.id.addReport));
        setUpReportTable();
        return view;
    }

    private void setUpView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_sleep, container, false);
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);

        View tableView = inflater.inflate(R.layout.report_table_view, container, false);
        View graphView = inflater.inflate(R.layout.report_graph_view, container, false);
        final ImageView graphsBtn = view.findViewById(R.id.graphs_button);
        final ImageView tableBtn = view.findViewById(R.id.table_button);

        mTableLayout = tableView.findViewById(R.id.tableReportLayout);
        mGraphsLayout = graphView.findViewById(R.id.graphsLayout);
        mEmptyTableError = tableView.findViewById(R.id.empty_table_error);

        final ViewPager viewPager = view.findViewById(R.id.viewPager);
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

    private void setUpGraphs(List<ReportEntry> entries) {
        // set up graph ui
        LineChartView chart = new LineChartView(view.getContext());
        mGraphsLayout.addView(chart);

        chart.setInteractive(true);
        chart.setScrollEnabled(true);
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        List<Line> lines = generateDataForGraph(entries);

        LineChartData data = new LineChartData();
        Axis axisY = new Axis().setHasLines(true);
        axisY.setName("Duration in minutes");
        data.setAxisXBottom(null);
        data.setAxisYLeft(axisY);
        data.setLines(lines);
        chart.setLineChartData(data);
        chart.setScrollEnabled(false);
        chart.setZoomEnabled(true);
    }

    private List<Line> generateDataForGraph(List<ReportEntry> reportEntries) {
        List<PointValue> values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        for (int j = 0; j < reportEntries.size(); ++j) {
            SleepEntry entry = (SleepEntry) reportEntries.get(j);
            PointValue pointValue = new PointValue(j, entry.getDuration());
            values.add(pointValue);
            Line line = new Line(values).setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)).setCubic(true);
            line.setHasLabels(true);
            lines.add(line);
        }
        return lines;
    }

    private void setUpReportTable() {
        final Context context = getContext();
        entriesViewModel.getSleepEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (entriesViewModel.isFirstTimeSleep()) {
                    if (reportEntries != null && reportEntries.size() > 0) {
                        mEmptyTableError.setVisibility(View.GONE);
                        reportEntries.sort(new EntryDataComparator());
                        SleepEntry titleEntry = (SleepEntry) reportEntries.get(0);
                        if (!titleEntry.getDate().equals("date")) {
                            reportEntries.add(0, new SleepEntry("date", "start", "end", "duration"));
                        }
                    } else {
                        // empty table
                        mEmptyTableError.setVisibility(View.VISIBLE);
                        reportEntries = new ArrayList<>();
                        reportEntries.add(0, new SleepEntry("date", "start", "end", "duration"));
                    }
                    mTableAdapter = new ReportTableAdapter(context, reportEntries);
                    mTableLayout.setAdapter(mTableAdapter);
                    mTableAdapter.notifyDataSetChanged();
                    setUpGraphs(reportEntries);
                    entriesViewModel.setIsFirstTimeSleep(false);
                } else {
                    entriesViewModel.setIsFirstTimeSleep(true);
                }
            }
        });
    }

    private void onAddClickListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpSleep popUpClass = new PopUpSleep(getActivity(), babyID, entriesViewModel);
                popUpClass.showPopupWindow(view);
            }
        });
    }

}
