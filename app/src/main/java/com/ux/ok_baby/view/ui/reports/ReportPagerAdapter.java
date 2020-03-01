package com.ux.ok_baby.view.ui.reports;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

class ReportPagerAdapter extends PagerAdapter {
    private ViewGroup tableView, graphView;
    private ImageView graphsBtn;

    public ReportPagerAdapter(View tableView, View graphView) {
        this.tableView = (ViewGroup) tableView;
        this.graphView = (ViewGroup) graphView;
    }

    public ReportPagerAdapter(View tableView, final View graphView, ImageView graphsBtn) {
        this.tableView = (ViewGroup) tableView;
        this.graphView = (ViewGroup) graphView;
        this.graphsBtn = graphsBtn;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ViewGroup layout;
        if (position == 0) {
            layout = tableView;
        } else {
            layout = graphView;
        }
        collection.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}