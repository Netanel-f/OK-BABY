package com.ux.ok_baby.view.ui.reports;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.ux.ok_baby.view.adapter.ReportTableAdapter;
import com.ux.ok_baby.view.popups.PopUpSleep;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.List;


/**
 * Contains the sleep report.
 */
public class SleepFragment extends Fragment {
    private final String TAG = "SleepFragment";
    private AdaptiveTableLayout mTableLayout;
    private ReportTableAdapter mTableAdapter;
    private Button graphsBtn, tableBtn;
    private String babyID;
    private EntriesViewModel entriesViewModel;

    public SleepFragment(String babyID) {
        this.babyID = babyID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);


        // bind
        mTableLayout = (AdaptiveTableLayout) view.findViewById(R.id.tableReportLayout);
        graphsBtn = (Button) view.findViewById(R.id.switch_to_graph_btn);
        tableBtn = (Button) view.findViewById(R.id.switch_to_table_btn);

        setUpReportTable(babyID);
        setUpGraphsBtn();
        onAddClickListener(view.findViewById(R.id.addReport));
        loadFromFirebase();
        return view;
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
//        CollectionReference dataSource = null; // todo - query from firebase
        entriesViewModel.getSleepEntries(babyID).observe(this, new Observer<List<ReportEntry>>() {
                    @Override
                    public void onChanged(List<ReportEntry> reportEntries) {
                        mTableAdapter = new ReportTableAdapter(getContext(), reportEntries);
                        mTableLayout.setAdapter(mTableAdapter);
                        mTableLayout.setHeaderFixed(true);
                        mTableLayout.setSolidRowHeader(false);
                        mTableAdapter.notifyDataSetChanged();
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
            }
        });
        tableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphsBtn.setVisibility(View.VISIBLE);
                tableBtn.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onAddClickListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpSleep popUpClass = new PopUpSleep(getActivity(), babyID);
                popUpClass.showPopupWindow(view);
            }
        });
    }
}
