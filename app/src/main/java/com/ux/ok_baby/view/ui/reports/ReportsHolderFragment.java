package com.ux.ok_baby.view.ui.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static com.ux.ok_baby.utils.Constants.*;

import com.google.android.material.tabs.TabLayout;
import com.ux.ok_baby.R;


public class ReportsHolderFragment extends Fragment {

    private int reportType = -1;
    private String babyID;

    public ReportsHolderFragment(String babyID) {
        this.babyID = babyID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_layout, container, false);
        reportType = getArguments().getInt(REPORT_TYPE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final ViewPager viewPager = view.findViewById(R.id.reports);
        viewPager.setAdapter(new ReportsPagerAdapter(getChildFragmentManager(),babyID));
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setUpTab(tabLayout);
    }

    private void setUpTab(TabLayout tabLayout) {
        if (reportType != -1) {
            tabLayout.getTabAt(reportType).select();
        }
    }
}

class ReportsPagerAdapter extends FragmentStatePagerAdapter {
    private final int NUM_OF_TABS = 3;
    private String babyID;

    public ReportsPagerAdapter(FragmentManager fm,String babyID) {
        super(fm);
        this.babyID = babyID;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case SLEEP_TAB:
                return new SleepFragment(babyID);
            case FOOD_TAB:
                return new FoodFragment(babyID);
            default:
                return new DiaperFragment(babyID);
        }
    }

    @Override
    public int getCount() {
        return NUM_OF_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case SLEEP_TAB:
                return SLEEP;
            case FOOD_TAB:
                return FOOD;
            default:
                return DIAPER;
        }
    }
}
