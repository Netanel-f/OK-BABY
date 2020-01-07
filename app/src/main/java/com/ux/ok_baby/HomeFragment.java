package com.ux.ok_baby;

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

    private void setUpOtherBabies(){
        TableRow tableRow = findViewById(R.id.otherBabies);
        int id = getResources().getIdentifier("ic_baby", "mipmap", getBaseContext().getPackageName());
        CircleImageView circleImageView = new CircleImageView(this);
        circleImageView.setImageResource(id);
        tableRow.addView(circleImageView);
        int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        circleImageView.getLayoutParams().height=dimensionInDp;
        circleImageView.getLayoutParams().width=dimensionInDp;
    }
}
