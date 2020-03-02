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
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.model.SleepEntry;
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpSleep;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class SleepFragment extends Fragment {
    private View view;
    private String babyID;
    private LinearLayout mGraphsLayout;
    private AdaptiveTableLayout mTableLayout;
    private ReportTableAdapter mTableAdapter;
    private EntriesViewModel entriesViewModel;
    private ConstraintLayout mEmptyTableError;
    private ImageView graphsButton, tableButton;

    /**
     * ctr for Sleep fragment
     * @param babyID baby id to open fragment with
     */
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


    /**
     * This method will setup the View UI elemnts
     * @param inflater Layout inflater
     * @param container ViewGroup container
     */
    private void setUpView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_sleep, container, false);
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);

        View tableView = inflater.inflate(R.layout.report_table_view, container, false);
        View graphView = inflater.inflate(R.layout.report_graph_view, container, false);

        graphsButton = view.findViewById(R.id.graphsButton);
        tableButton = view.findViewById(R.id.tableButton);

        mTableLayout = tableView.findViewById(R.id.tableReportLayout);
        mGraphsLayout = graphView.findViewById(R.id.graphsLayout);
        mEmptyTableError = tableView.findViewById(R.id.emptyTableError);

        final ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ReportPagerAdapter(tableView, graphView));
        setUpButtons(viewPager);
    }


    /**
     * This mehod will setup the functionality of UI buttons
     * @param viewPager view page to update
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
     * this method will toggle the graph / table
     * @param destination
     * @param viewPager
     * @param graphsBtn
     * @param tableBtn
     */
    private void toggleGraphAndTable(int destination, ViewPager viewPager, ImageView graphsBtn, ImageView tableBtn) {
        viewPager.setCurrentItem(destination, true);
        toggleButtonVisibility(graphsBtn);
        toggleButtonVisibility(tableBtn);
    }


    /**
     * This metho will toggle the button visibility
     * @param button ImageView button to toggle/
     */
    private void toggleButtonVisibility(ImageView button) {
        if (button.getVisibility() == View.VISIBLE) {
            button.setVisibility(View.INVISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }


    /**
     * This method will setup the graph
     * @param entries entries to display in graph
     */
    private void setUpGraphs(List<ReportEntry> entries) {
        LineChartView chart = new LineChartView(view.getContext());
        mGraphsLayout.addView(chart);

        chart.setInteractive(true);
        chart.setScrollEnabled(true);
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        List<Line> lines = generateDataForGraph(entries);

        Axis axisY = new Axis().setHasLines(true);
        axisY.setName("Duration in minutes");

        LineChartData data = new LineChartData();
        data.setAxisXBottom(null);
        data.setAxisYLeft(axisY);
        data.setLines(lines);
        chart.setLineChartData(data);
        chart.setScrollEnabled(false);
        chart.setZoomEnabled(true);
    }


    /**
     * This method will generate List of line from the list of ReportEntries
     * @param reportEntries List of ReportEntry types.
     * @return list of Lines.
     */
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



    /**
     * If reportEntries is empty, add titles. otherwise, sort reportEntries and add titles if not exist.
     * @param reportEntries List of ReportEntry types.
     */
    private void updateReportEntries(List<ReportEntry> reportEntries) {
        if (reportEntries != null && reportEntries.size() > 0) {
            mEmptyTableError.setVisibility(View.GONE);
            reportEntries.sort(new EntryDataComparator());
            SleepEntry titleEntry = (SleepEntry) reportEntries.get(0);
            if (!titleEntry.getDate().equals("date")) {
                reportEntries.add(0, new SleepEntry("date", "start", "end", "duration"));
            }
        } else {
            mEmptyTableError.setVisibility(View.VISIBLE);
            reportEntries = new ArrayList<>();
            reportEntries.add(0, new SleepEntry("date", "start", "end", "duration"));
        }
    }


    /**
     * setup the report table.
     */
    private void setUpReportTable() {
        final Context context = getContext();
        entriesViewModel.getSleepEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (entriesViewModel.isFirstTimeSleep()) {
                    updateReportEntries(reportEntries);
                    if (!reportEntries.isEmpty()) {
                        mTableAdapter = new ReportTableAdapter(context, reportEntries);
                        mTableLayout.setAdapter(mTableAdapter);
                        mTableAdapter.notifyDataSetChanged();
                        setUpGraphs(reportEntries);
                        entriesViewModel.setIsFirstTimeSleep(false);
                    }
                } else {
                    entriesViewModel.setIsFirstTimeSleep(true);
                }
            }
        });
    }


    /**
     * set on click lister to the view
     * @param view View to set on
     */
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
