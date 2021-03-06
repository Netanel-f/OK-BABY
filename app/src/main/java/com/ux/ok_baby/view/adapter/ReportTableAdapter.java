package com.ux.ok_baby.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.ReportEntry;

import java.util.List;

import static com.ux.ok_baby.utils.Constants.POO_COLORS1;

public class ReportTableAdapter extends LinkedAdaptiveTableAdapter<ViewHolderImpl> {

    private static final String TAG = "ReportTableAdapter";
    private static final int NUM_OF_COLS_IN_REPORT = 6;
    private final List<ReportEntry> mTableDataSource;
    private final LayoutInflater mLayoutInflater;
    private final int mHeaderHeight;
    private final int mColumnWidth;
    private final int mHeaderWidth;
    private final int mRowHeight;


    public ReportTableAdapter(Context context, List<ReportEntry> dataSource) {
        mLayoutInflater = LayoutInflater.from(context);
        mTableDataSource = dataSource;

        Resources res = context.getResources();
        mColumnWidth = res.getDimensionPixelSize(R.dimen.col_width);
        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height);
        mHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height);
        mHeaderWidth = res.getDimensionPixelSize(R.dimen.row_header_width);
    }

    @Override
    public int getRowCount() {
        return mTableDataSource.size();
    }

    @Override
    public int getColumnCount() {
        if (mTableDataSource.isEmpty()) {
            return NUM_OF_COLS_IN_REPORT;
        } else {
            return Math.min(NUM_OF_COLS_IN_REPORT, mTableDataSource.get(0).getNumOfDisplayedFields());
        }
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateItemViewHolder(@NonNull ViewGroup parent) {
        return new TestViewHolder(mLayoutInflater.inflate(R.layout.item_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.item_header_column, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.item_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderLeftTopViewHolder(mLayoutInflater.inflate(R.layout.item_header_column, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, int row, int column) {
        final TestViewHolder vh = (TestViewHolder) viewHolder;

        try {
            ReportEntry entry = mTableDataSource.get(row);
            String itemData = entry.getDataByField(column);
            itemData = itemData.trim();

            if (column == 3 && mTableDataSource.get(0).getDataByField(3).equals("texture") && !itemData.isEmpty()) {
                SpannableString spannable2 = getSpannableString(itemData, entry);
                vh.tvText.setVisibility(View.VISIBLE);
                vh.tvText.setText(spannable2);
            } else {
                vh.tvText.setVisibility(View.VISIBLE);
                vh.tvText.setText(itemData);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: table data not updated.");
        }
    }

    private SpannableString getSpannableString(String itemData, ReportEntry entry) {
        SpannableString spannable2 = new SpannableString(itemData);
        try {
            spannable2.setSpan(new ForegroundColorSpan(POO_COLORS1.get(entry.getDataByField(4))),
                    0, itemData.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (NullPointerException e) {
            Log.d(TAG, "onBindViewHolder: Poo color not set.");
            spannable2.setSpan(new ForegroundColorSpan(POO_COLORS1.get("Brown")),
                    0, itemData.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable2;
    }


    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;
        vh.vLine.setBackgroundColor(Color.WHITE);
        if (column < NUM_OF_COLS_IN_REPORT) {
            ReportEntry titleEntry = mTableDataSource.get(0);
            vh.tvText.setText(titleEntry.getDataByField(column));
        } else {
            vh.tvText.setText("col head");  // skip left top header
        }
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
        ReportEntry entry = mTableDataSource.get(row);
        String itemData = entry.getDataByField(0);
        vh.tvText.setText(itemData);
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
        ReportEntry titleEntry = mTableDataSource.get(0);
        vh.tvText.setText(titleEntry.getDataByField(0));
    }

    @Override
    public int getColumnWidth(int column) {
        if (mTableDataSource.isEmpty()) {
            return mColumnWidth;
        } else {
            return Math.max(calculateColWidth(getColumnCount()), mColumnWidth);
        }
    }

    @Override
    public int getHeaderColumnHeight() {
        return mHeaderHeight;
    }

    @Override
    public int getRowHeight(int row) {
        return mRowHeight;
    }

    @Override
    public int getHeaderRowWidth() {
        return mHeaderWidth;
    }


    private int calculateColWidth(int numOfCols) {
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int padding = 80;
        return (int) Math.floor((width - padding) / numOfCols);
    }


    //------------------------------------- view holders ------------------------------------------

    private static class TestViewHolder extends ViewHolderImpl {
        TextView tvText;


        private TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
        }
    }

    private static class TestHeaderColumnViewHolder extends ViewHolderImpl {
        TextView tvText;
        View vLine;

        private TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
            vLine = itemView.findViewById(R.id.vLine);
        }
    }

    private static class TestHeaderRowViewHolder extends ViewHolderImpl {
        TextView tvText;

        TestHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
        }
    }

    private static class TestHeaderLeftTopViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderLeftTopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
        }
    }
}
