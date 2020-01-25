package com.ux.ok_baby.view.ui.reports;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
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
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * Contains the sleep report.
 */
public class SleepFragment extends Fragment {
    private final String TAG = "SleepFragment";
    private AdaptiveTableLayout mTableLayout;
    private ReportTableAdapter mTableAdapter;
    private LinearLayout mGraphsLayout;
//    private HorizontalScrollView mGraphsLayout;
    private Button graphsBtn, tableBtn;
    private String babyID;
    private EntriesViewModel entriesViewModel;
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
        mTableLayout = (AdaptiveTableLayout) view.findViewById(R.id.tableReportLayout);
        mGraphsLayout = (LinearLayout) view.findViewById(R.id.sleepGraphsLayout);
        graphsBtn = (Button) view.findViewById(R.id.switch_to_graph_btn);
        tableBtn = (Button) view.findViewById(R.id.switch_to_table_btn);

        setUpReportTable(babyID);
        setUpGraphsBtn();
        onAddClickListener(view.findViewById(R.id.addReport));
        loadFromFirebase();
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
            PointValue pointValue = new PointValue(j, ReportTableAdapter.calculateDurationInt(entry));
            values.add(pointValue);
            Line line = new Line(values).setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)).setCubic(true);
            line.setHasLabels(true);
//            line.setPointColor(ChartUtils.COLORS[j % ChartUtils.COLORS.length]);
            lines.add(line);
        }

        boolean hasAxes = true;
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
//        } else {
//            data.setAxisXBottom(null);
//            data.setAxisYLeft(null);
//        }

//        data.setBaseValue(0);
        data.setLines(lines);
        chart.setLineChartData(data);

//        chart.setViewportCalculationEnabled(false);
//        resetViewport(chart, entries.size());

        Viewport v = new Viewport(chart.getMaximumViewport());
        v.left = 0;
//        v.left = axisX.getValues().size() - 0.5f;
        v.right = v.right - 3;
//        v.top = axisY.getValues().size() - 1;
//        v.bottom = axisY.getValues().size();
//        chart.setCurrentViewport(v);
//        chart.setViewportCalculationEnabled(false);
        chart.setCurrentViewportWithAnimation(v);
        chart.setScrollEnabled(true);
        chart.setZoomEnabled(false);

    }


    private void resetViewport(LineChartView chart, int numberOfPoints) {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 3;
        v.left = 0;
        v.right = numberOfPoints - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    private void loadFromFirebase() {
        CollectionReference sleepCollection = FirebaseFirestore.getInstance().collection("babies")
                .document(babyID).collection("sleep_reports");
        sleepCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            // TODO: 1/12/2020 add row to table
                            break;
                        case MODIFIED:
                            // TODO: 1/12/2020 update row in table
                            break;
                        case REMOVED:
                            // TODO: 1/12/2020 remove row from table
                            break;
                    }
                }
            }
        });
    }

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
                                    return s1.getDate().compareTo(s2.getDate());
                                }
                            });
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

    private void setUpGraphsBtn() {
        graphsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphsBtn.setVisibility(View.GONE);
                tableBtn.setVisibility(View.VISIBLE);
                mTableLayout.setVisibility(View.GONE);
                mGraphsLayout.setVisibility(View.VISIBLE);
            }
        });
        tableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphsBtn.setVisibility(View.VISIBLE);
                tableBtn.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.VISIBLE);
                mGraphsLayout.setVisibility(View.GONE);
            }
        });
    }

    private void onAddClickListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpSleep popUpClass = new PopUpSleep(getActivity(), babyID,entriesViewModel);
                popUpClass.showPopupWindow(view);
            }
        });
    }
}
