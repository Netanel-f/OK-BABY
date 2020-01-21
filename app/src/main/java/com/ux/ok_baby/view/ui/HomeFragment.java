package com.ux.ok_baby.view.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.ux.ok_baby.R;
import com.ux.ok_baby.model.Baby;
import com.ux.ok_baby.model.User;
import com.ux.ok_baby.utils.Constants;
import com.ux.ok_baby.viewmodel.EntriesViewModel;
import com.ux.ok_baby.viewmodel.UserViewModel;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
//import java.time.LocalDate;
//import java.time.Period;
//import java.time.format.DateTimeFormatter;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
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

    private ImageView babyImgView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        babyImgView = findViewById(R.id.babyImage);

        userID = getIntent().getStringExtra(USER_ID_TAG); //TODO change?
        Boolean isNewUser = getIntent().getBooleanExtra(IS_NEW_USER_TAG, true);
        babyID = getIntent().getStringExtra(Constants.BABY_ID);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class); //todo fix deprecated
//        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        if (isNewUser) {
            addNewBaby();

        } else {
            userViewModel.getBaby(babyID).observe(this, new Observer<Baby>() {
                @Override
                public void onChanged(Baby babyChanged) {
                    baby = babyChanged;
                    setUpBabyDetails();
                }
            });
        }

        setUpMenu(savedInstanceState);
        setupEditButton();
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

    private void setupEditButton() {
        babyImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCurrentBaby();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // returning from edit a new baby profile
        if (requestCode == START_BABY_PROF_ACT && resultCode == RESULT_OK) {
            if (data == null ) {
                Log.d(TAG, "onActivityResult: START_BABY_PROF_ACT and data == null");

            } else {
                baby = data.getParcelableExtra(BABY_OBJECT_TAG);
                userViewModel.addBaby(userID, baby);
                setUpBabyDetails();
            }
        }

        // returning from edit an existing baby profile
        if (requestCode == START_BABY_PROF_EDIT_ACT && resultCode == RESULT_OK) {
            if (data == null) {
                Log.d(TAG, "onActivityResult: START_BABY_PROF_EDIT_ACT and data == null");

            } else {
                baby = data.getParcelableExtra(BABY_OBJECT_TAG);
                userViewModel.updateBaby(baby);
                setUpBabyDetails();
            }
        }
    }

    private void setUpBabyDetails() {
        ((TextView) findViewById(R.id.babyName)).setText(baby.getBabyName());
        ((TextView) findViewById(R.id.babyAge)).setText(getAgeString(baby.getBabyDOB()));
        //todo update photo
        Glide.with(this)
                .load(baby.getImageUrl())
                .placeholder(R.mipmap.ic_baby)
                .error(R.mipmap.ic_baby)
                .apply(RequestOptions.circleCropTransform())
                .into(babyImgView);

    }

    public String getAgeString(String dob) {
        DateTimeFormatter format = DateTimeFormat.forPattern(DATE_PATTERN);
        DateTime date = format.parseDateTime(dob);
        DateTime today = new DateTime();

        int monthsAge = Months.monthsBetween(date, today).getMonths();

        if (monthsAge == 0) {
            return Days.daysBetween(date, today).getDays() + " days old";
        } else {
            return monthsAge + " months old";
        }

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

        Log.d(TAG, "starting BabyProfileActivity for result with baby id: " + babyID + " uid: " + userID);
        startActivityForResult(intent, START_BABY_PROF_ACT);
    }

    private void editCurrentBaby() {
        Intent intent = new Intent(this, BabyProfileActivity.class);
        intent.putExtra(BABY_OBJECT_TAG, baby);

        Log.d(TAG, "starting BabyProfileActivity for result with baby id: " + babyID + " uid: " + userID);
        startActivityForResult(intent, START_BABY_PROF_EDIT_ACT);
    }
}
