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
import com.ux.ok_baby.model.EntryDataComparator;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.model.SleepEntry;
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpSleep;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.ArrayList;
import java.util.Comparator;
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

        mTableLayout = tableView.findViewById(R.id.tableReportLayout);
        mGraphsLayout = graphView.findViewById(R.id.graphsLayout);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ReportPagerAdapter(tableView, graphView));
    }

    private void setUpGraphs(List<ReportEntry> entries) {
        // set up graph ui
        LineChartView chart = new LineChartView(view.getContext());
        mGraphsLayout.addView(chart);

        chart.setInteractive(true);
        chart.setScrollEnabled(true);
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        // add values to graph
        List<PointValue> values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        for (int j = 0; j < entries.size(); ++j) {
            SleepEntry entry = (SleepEntry) entries.get(j);
//            PointValue pointValue = new PointValue(j, ReportTableAdapter.calculateDurationInt(entry));
            PointValue pointValue = new PointValue(j, entry.getDuration());
            values.add(pointValue);
            Line line = new Line(values).setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)).setCubic(true);
            line.setHasLabels(true);
//            line.setPointColor(ChartUtils.COLORS[j % ChartUtils.COLORS.length]);
            lines.add(line);
        }

        boolean hasAxesNames = true;
        LineChartData data = new LineChartData();

        Axis axisX;
        Axis axisY;
//        if (hasAxes) {
        axisX = new Axis();
        axisY = new Axis().setHasLines(true);
        if (hasAxesNames) {
//                axisX.setName("Axis X");
            axisY.setName("Duration in minutes");
        }
        data.setAxisXBottom(null);
        data.setAxisYLeft(axisY);
        data.setLines(lines);
        chart.setLineChartData(data);

//        Viewport v = new Viewport(chart.getMaximumViewport());
//        v.left = 0;
//        v.right = v.right - 0.5f;
//        chart.setCurrentViewportWithAnimation(v);
        chart.setScrollEnabled(false);
        chart.setZoomEnabled(true);
    }

    private void setUpReportTable() {
        entriesViewModel.getSleepEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (reportEntries != null && reportEntries.size() > 0) {
                    // todo: remove sort from here- maybe in viewmodel when getting entries
//                    reportEntries.sort(new Comparator<ReportEntry>() {
//                        @Override
//                        public int compare(ReportEntry o1, ReportEntry o2) {
//                            SleepEntry s1 = (SleepEntry) o1;
//                            SleepEntry s2 = (SleepEntry) o2;
//
//                            // handle title row
//                            if (s1.getDate().equals("date")) {
//                                return -1;
//                            } else if (s2.getDate().equals("date")) {
//                                return 1;
//                            }
//
//                            return s1.getDate().compareTo(s2.getDate());
//                        }
//                    });
                    reportEntries.sort(new EntryDataComparator());

                    // todo: temp
                    SleepEntry titleEntry = (SleepEntry) reportEntries.get(0);
                    if (!titleEntry.getDate().equals("date")) {
                        reportEntries.add(0, new SleepEntry("date", "start", "end", "duration"));
                    }

                    mTableAdapter = new ReportTableAdapter(getContext(), reportEntries);
                    mTableLayout.setAdapter(mTableAdapter);
                    mTableAdapter.notifyDataSetChanged();
                    setUpGraphs(reportEntries);
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
