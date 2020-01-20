package com.ux.ok_baby.view.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ux.ok_baby.BabyProfileActivity;
import com.ux.ok_baby.MenuFragment;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.Baby;
import com.ux.ok_baby.viewmodel.UserViewModel;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ux.ok_baby.utils.Constants.*;

/**
 * First screen when loading the app (after sign in).
 * Contains buttons to navigate to other screens.
 */
public class HomeFragment extends FragmentActivity {
    private CollectionReference babiesCollection = FirebaseFirestore.getInstance().collection("babies");
    private final String TAG = "HomeFragment";
    private UserViewModel userViewModel;
    private String babyID, userID;
    private Baby baby;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        userID = getIntent().getStringExtra(USER_ID_TAG); //TODO change?
        Boolean isNewUser = getIntent().getBooleanExtra(IS_NEW_USER_TAG, true);


//        if (babyID == null) {
        if (isNewUser) {
            addNewBaby();
        }

        setUpBabyDetails();
        setUpMenu(savedInstanceState);
        setUpOtherBabies();
    }

    private void setUpMenu(@Nullable Bundle savedInstanceState) {
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            MenuFragment menuFragment = new MenuFragment(babyID);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_BABY_PROF_ACT && resultCode == RESULT_OK) {
            baby = data.getParcelableExtra(BABY_OBJECT_TAG);
            userViewModel.addBaby(userID, baby);
            setUpBabyDetails();
        }
    }

    private void setUpBabyDetails() {
        ((TextView) findViewById(R.id.babyName)).setText(baby.getBabyName());
        ((TextView) findViewById(R.id.babyAge)).setText(getAgeByMonths(baby.getBabyDOB()) + " months old");
    }

    public String getAgeByMonths(String dob) {
        DateTimeFormatter format = DateTimeFormat.forPattern(DATE_PATTERN);
        DateTime date = format.parseDateTime(dob);
        DateTime today = new DateTime();
        return Integer.toString(Months.monthsBetween(date, today).getMonths());
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

    private void loadOtherBabies() {
        // TODO: 1/9/2020  load other babies from firebase.
    }

    private void addNewBaby() {
        babyID = babiesCollection.document().getId();
        baby = new Baby(babyID);
        Intent intent = new Intent(this, BabyProfileActivity.class);
        intent.putExtra(BABY_OBJECT_TAG, baby);
        startActivityForResult(intent, START_BABY_PROF_ACT);
    }
}
