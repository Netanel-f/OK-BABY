package com.ux.ok_baby.view.ui.reports;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ux.ok_baby.R;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.model.SleepEntry;
import com.ux.ok_baby.viewmodel.EntriesViewModel;

import java.util.List;


/**
 * Contains the other report.
 */
public class OtherFragment extends Fragment {

    private EntriesViewModel entriesViewModel;


    public OtherFragment(String babyID) {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        final TextView text = view.findViewById(R.id.otherText);

        // getting sleep entries example todo: delete
        String bid = "69kkdHZH48TOYdXWq1hP";
        entriesViewModel = new ViewModelProvider(getActivity()).get(EntriesViewModel.class);
        entriesViewModel.getSleepEntries(bid).observe(this, new Observer<List<ReportEntry>>() {
            @Override
            public void onChanged(List<ReportEntry> reportEntries) {
                text.setText(reportEntries.toString());
            }
        });

        // adding sleep entry example todo: delete
        SleepEntry entry = new SleepEntry("01/14/20", "21:50", "21:59");
        entriesViewModel.addSleepEntry(bid, entry);

        return view;
    }

}
