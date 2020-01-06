//package com.ux.ok_baby;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentStatePagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//
//public class BabyDetailsFragment extends Fragment {
//    private ViewPager viewPager;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.body_layout, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        BabyDetailsAdapter babyDetailsAdapter= new BabyDetailsAdapter(getChildFragmentManager());
//        viewPager = view.findViewById(R.id.sushi_fillings);
//        viewPager.setAdapter(babyDetailsAdapter);
//    }
//
//}
//
//class BabyDetailsAdapter extends FragmentStatePagerAdapter {
//
//    public BabyDetailsAdapter(FragmentManager fm) {
//        super(fm);
//    }
//
//    @Override
//    public Fragment getItem(int i) {
//        if (i == 0)
//            return new MenuFragment();
//        else // TODO: 1/4/2020 add all options
//            return null;
//    }
//
//    @Override
//    public int getCount() {
//        return 2;
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        if (position == 0)
//            return "Menu";
//        else // TODO: 1/4/2020 update
//            return "Vegetables";
//    }
//}
//
