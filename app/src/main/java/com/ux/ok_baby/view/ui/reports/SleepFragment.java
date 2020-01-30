package com.ux.ok_baby.view.ui.reports;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import com.ux.ok_baby.R;
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
import lecho.lib.hellocharts.model.Viewport;
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
    //    private HorizontalScrollView mGraphsLayout;
    private Button graphsBtn, tableBtn;
    private String babyID;
    private View view;

    public SleepFragment(String babyID) {
        this.babyID = babyID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sleep, container, false);
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);

        // bind
        mTableLayout = (AdaptiveTableLayout) view.findViewById(R.id.sleepTableReportLayout);
        mGraphsLayout = (LinearLayout) view.findViewById(R.id.sleepGraphsLayout);

        // todo: move
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                view.findViewById(R.id.sleepBottomNavBar);

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


        onAddClickListener(view.findViewById(R.id.addReport));
        setUpReportTable(babyID);

        return view;
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

//    private void loadFromFirebase() {
//        CollectionReference sleepCollection = FirebaseFirestore.getInstance().collection("babies")
//                .document(babyID).collection("sleep_reports");
//        sleepCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshots,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w(TAG, "listen:error", e);
//                    return;
//                }
//
//                for (DocumentChange dc : snapshots.getDocumentChanges()) {
//                    switch (dc.getType()) {
//                        case ADDED:
//                            // TODO: 1/12/2020 add row to table
//                            break;
//                        case MODIFIED:
//                            // TODO: 1/12/2020 update row in table
//                            break;
//                        case REMOVED:
//                            // TODO: 1/12/2020 remove row from table
//                            break;
//                    }
//                }
//            }
//        });
//    }

    private void setUpReportTable(String babyID) {
        entriesViewModel.getSleepEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                if (reportEntries != null && reportEntries.size() > 0) {
                    // todo: remove sort from here- maybe in viewmodel when getting entries
                    reportEntries.sort(new Comparator<ReportEntry>() {
                        @Override
                        public int compare(ReportEntry o1, ReportEntry o2) {
                            SleepEntry s1 = (SleepEntry) o1;
                            SleepEntry s2 = (SleepEntry) o2;

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
