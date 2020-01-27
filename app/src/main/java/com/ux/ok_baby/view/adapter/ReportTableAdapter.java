package com.ux.ok_baby.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.model.SleepEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportTableAdapter  extends LinkedAdaptiveTableAdapter<ViewHolderImpl> {

    private static final int NUM_OF_COLS_IN_REPORT = 4;
    private final LayoutInflater mLayoutInflater;
    private final List<ReportEntry> mTableDataSource;
    private final int mColumnWidth;
    private final int mRowHeight;
    private final int mHeaderHeight;
    private final int mHeaderWidth;


    public ReportTableAdapter(Context context, List<ReportEntry> dataSource) {
        mLayoutInflater = LayoutInflater.from(context);
        mTableDataSource = dataSource;

        // todo - change sizes? these are copied
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
        return NUM_OF_COLS_IN_REPORT; // todo: change
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

        ReportEntry entry = mTableDataSource.get(row);
        String itemData;
        itemData = entry.getDataByField(column);
        itemData = itemData.trim();

        // update views
        vh.tvText.setVisibility(View.VISIBLE);
        vh.tvText.setText(itemData);
    }


    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;
        vh.vLine.setBackgroundColor(Color.WHITE);
        if (column < NUM_OF_COLS_IN_REPORT){
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
        vh.tvText.setText(itemData); // todo change

    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
        ReportEntry titleEntry = mTableDataSource.get(0);
        vh.tvText.setText(titleEntry.getDataByField(0));

    }

    @Override
    public int getColumnWidth(int column) {
        if (mTableDataSource.isEmpty() || mTableDataSource.size()<=1){
            return mColumnWidth;
        } else {
            // determine col width dynamically
            return Math.max(mTableDataSource.get(1).getDataByField(column).length() * 20 + 50, mColumnWidth);
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
        String[] colTitles = {"date", "start", "end", "duration"};

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
