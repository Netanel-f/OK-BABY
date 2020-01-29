package com.ux.ok_baby.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.ux.ok_baby.R;
import com.ux.ok_baby.model.Baby;
import com.ux.ok_baby.utils.Constants;
import com.ux.ok_baby.viewmodel.UserViewModel;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    boolean isNewUser;

    private TextView mainBabyName;
    private TextView mainBabyAge;
    private ImageView babyImgView;

    static List<Baby> userBabies;
    private BabyRecyclerUtils.BabyAdapter otherBabiesAdapter = new BabyRecyclerUtils.BabyAdapter();
    private RecyclerView babiesRecyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        // set up views
        babyImgView = findViewById(R.id.babyImage);
        mainBabyName = findViewById(R.id.babyName);
        mainBabyAge = findViewById(R.id.babyAge);
        babiesRecyclerView = findViewById(R.id.babiesRecycler);

        extractIntentData();
        setUpRecycler();


        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class); //todo fix deprecated
//        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        if (isNewUser) {
            addNewBaby();

        } else {
            userViewModel.getUserBabies(userID).observe(this, new Observer<List<Baby>>() {
                @Override
                public void onChanged(List<Baby> babies) {
                    updateBabiesDetails(babies);
                }
            });
        }


        setUpMenu(savedInstanceState);
        setupEditButton();
    }


    private void updateBabiesDetails(List<Baby> babies) {
        baby = babies.remove(0);
        setUpMainBabyDetails();
        userBabies = babies;
        otherBabiesAdapter.submitList(userBabies);
    }


    private void extractIntentData() {
        isNewUser = getIntent().getBooleanExtra(IS_NEW_USER_TAG, true);
        userID = getIntent().getStringExtra(USER_ID_TAG);
        babyID = getIntent().getStringExtra(Constants.BABY_ID);

    }

    private void setUpRecycler() {
        babiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        babiesRecyclerView.setAdapter(otherBabiesAdapter);
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
                setUpMainBabyDetails();
            }
        }

        // returning from edit an existing baby profile
        if (requestCode == START_BABY_PROF_EDIT_ACT && resultCode == RESULT_OK) {
            if (data == null) {
                Log.d(TAG, "onActivityResult: START_BABY_PROF_EDIT_ACT and data == null");

            } else {
                baby = data.getParcelableExtra(BABY_OBJECT_TAG);
                userViewModel.updateBaby(baby);
                setUpMainBabyDetails();
            }
        }
    }

    private void setUpMainBabyDetails() {
        mainBabyName.setText(baby.getBabyName());
        mainBabyAge.setText(getAgeString(baby.getBabyDOB()));
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
