package com.ux.ok_baby;

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

import static com.ux.ok_baby.Constants.*;

import com.google.android.material.tabs.TabLayout;


public class ReportsHolderFragment extends Fragment {

    private String reportType = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_layout, container, false);
        reportType = getArguments().getString(REPORT_TYPE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewPager viewPager = view.findViewById(R.id.reports);
        viewPager.setAdapter(new ReportsPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setUpTab(tabLayout);
    }

    private void setUpTab(TabLayout tabLayout) {
        if (reportType != null) {
            switch (reportType) {
                case SLEEP:
                    tabLayout.getTabAt(0).select();
                    break;
                case FOOD:
                    tabLayout.getTabAt(1).select();
                    break;
                case DIAPER:
                    tabLayout.getTabAt(2).select();
                    break;
                case OTHER:
                    tabLayout.getTabAt(3).select();
                    break;
            }
        }
    }
}

class ReportsPagerAdapter extends FragmentStatePagerAdapter {
    private final int NUM_OF_TABS = 4;

    public ReportsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new SleepFragment();
            case 1:
                return new FoodFragment();
            case 2:
                return new DiaperReport();
        }
        return new OtherFragment();
    }

    @Override
    public int getCount() {
        return NUM_OF_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return SLEEP;
            case 1:
                return FOOD;
            case 2:
                return DIAPER;
        }
        return OTHER;
    }
}
