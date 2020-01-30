package com.ux.ok_baby.view.ui.reports;


import android.app.PendingIntent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.DiaperEntry;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpDiaper;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lecho.lib.hellocharts.formatter.AxisValueFormatter;
import lecho.lib.hellocharts.formatter.ValueFormatterHelper;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

import static com.ux.ok_baby.utils.Constants.PEE;
import static com.ux.ok_baby.utils.Constants.POO;


/**
 * Contains the diaper report.
 */
public class DiaperFragment extends Fragment {
    private EntriesViewModel entriesViewModel;
    private AdaptiveTableLayout mTableLayout;
    private LinearLayout mGraphsLayout;
    private ReportTableAdapter mTableAdapter;
    private Button graphsBtn, tableBtn;
    private String babyID;
    private View view;

    public DiaperFragment(String babyID) {
        this.babyID = babyID;
    }
    private final String TAG = "DiaperFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_diaper, container, false);
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);

        // bind
        mTableLayout = (AdaptiveTableLayout) view.findViewById(R.id.diaperTableReportLayout);
        mGraphsLayout = (LinearLayout) view.findViewById(R.id.diaperGraphsLayout);

        // todo: move
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                view.findViewById(R.id.diaperBottomNavBar);

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


//        setUpGraphsBtn();
        setUpReportTable(babyID);
//        loadFromFirebase();
        onAddClickListener(view.findViewById(R.id.addReport));

        return view;
    }

    private void loadFromFirebase() {
        CollectionReference diaperCollection = FirebaseFirestore.getInstance().collection("babies")
                .document(babyID).collection("diaper_reports");
        diaperCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }
            }
        });
    }

    private void setUpReportTable(String babyID) {
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

            if (j != i){
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
                value.setLabel((Integer) Math.round(value.getValue())+"");
                values.add(value);
            }

            Column column = new Column(values);

//            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
        }

        return columns;
    }
//
//        int[] colors = {ContextCompat.getColor(getContext(), R.color.colorPrimary),
//                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)};
//
//        for (int i = 0; i < numValues; ++i) {
//
//            SliceValue sliceValue = new SliceValue((float) numOfEntries[i], colors[i % numValues]);
//            values.add(sliceValue);
//        }
//
//        return values;
//
//    }

//    private void setUpGraphsBtn() {
//        graphsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                graphsBtn.setVisibility(View.GONE);
//                tableBtn.setVisibility(View.VISIBLE);
//                mTableLayout.setVisibility(View.GONE);
//            }
//        });
//        tableBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                graphsBtn.setVisibility(View.VISIBLE);
//                tableBtn.setVisibility(View.GONE);
//                mTableLayout.setVisibility(View.VISIBLE);
//            }
//        });
//    }

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
