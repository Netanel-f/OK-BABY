package com.ux.ok_baby;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TableRow;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * First screen when loading the app (after sign in).
 * Contains buttons to navigate to other screens.
 */
public class HomeFragment extends FragmentActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        // TODO: 1/4/2020 get baby details from parent activity.

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            MenuFragment menuFragment = new MenuFragment();
//            menuFragment.setArguments(intent.getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment).commit();
        }
        setUpOtherBabies();
    }

    private void setUpBabyDetails(String name, String age) {

    }


    @SuppressLint("ResourceAsColor")
    private void setUpOtherBabies() {
        TableRow tableRow = findViewById(R.id.otherBabies);
        CircleImageView circleImageView = new CircleImageView(this);
        circleImageView.setImageResource(getResources().getIdentifier("ic_baby", "mipmap", getBaseContext().getPackageName()));
        tableRow.addView(circleImageView);
        circleImageView.setBorderColor(getResources().getColor(R.color.light_gray));
        circleImageView.setBorderWidth(valToDp(2));
        circleImageView.getLayoutParams().height = valToDp(40);
        circleImageView.getLayoutParams().width = valToDp(40);
    }

    private int valToDp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private void onOtherBabyImageClick() {
        // TODO: 1/9/2020  switch all the data to be of the clicked baby.
    }
}
