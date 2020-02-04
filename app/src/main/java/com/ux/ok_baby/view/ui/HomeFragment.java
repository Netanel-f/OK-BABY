package com.ux.ok_baby.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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


import java.util.ArrayList;
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
public class HomeFragment extends FragmentActivity implements BabyRecyclerUtils.BabyClickCallback {
    private CollectionReference babiesCollection = FirebaseFirestore.getInstance().collection("babies");
    private final String TAG = "HomeFragment";
    private UserViewModel userViewModel;
    private String babyID, userID;
    private Baby mainBaby;
    private Baby tempMainBaby;
    boolean isNewUser;

    private TextView mainBabyName;
    private TextView mainBabyAge;
    private ImageView babyImgView;
    private ImageView addButtonImgView;
    static List<Baby> userBabies;
    private BabyRecyclerUtils.BabyAdapter otherBabiesAdapter = new BabyRecyclerUtils.BabyAdapter();
    private RecyclerView babiesRecyclerView;
    private MenuFragment menuFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        tempMainBaby = null;

        // set up views
        babyImgView = findViewById(R.id.babyImage);
        mainBabyName = findViewById(R.id.babyName);
        mainBabyAge = findViewById(R.id.babyAge);
        addButtonImgView = findViewById(R.id.addBabyButton);
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
                    mainBaby = babies.remove(0);
                    updateBabiesDetails(babies);
                }
            });
        }

        setUpMenu(savedInstanceState);
        setupEditButton();
        setUpAddBabyButton();
    }


    /**
     * this method will update other babies layout
     *
     * @param babies other babies to display on screen
     */
    private void updateBabiesDetails(List<Baby> babies) {
        setUpMainBabyDetails();
        userBabies = babies;
        otherBabiesAdapter.submitList(userBabies);
    }

    /**
     * This method will extract default Intent Data
     */
    private void extractIntentData() {
        isNewUser = getIntent().getBooleanExtra(IS_NEW_USER_TAG, true);
        userID = getIntent().getStringExtra(USER_ID_TAG);
        babyID = getIntent().getStringExtra(Constants.BABY_ID);
    }

    /**
     * This method set up the recycler utilities
     */
    private void setUpRecycler() {
        babiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        babiesRecyclerView.setAdapter(otherBabiesAdapter);
        otherBabiesAdapter.callback = this;
    }


    private void setUpMenu(@Nullable Bundle savedInstanceState) {
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            menuFragment = new MenuFragment(babyID);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment).commit();
        }
    }

    /**
     * This method set up the edit button listenr
     */
    private void setupEditButton() {
        babyImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCurrentBaby();
            }
        });
    }

    /**
     * This method set up the add baby button listner
     */
    private void setUpAddBabyButton() {
        addButtonImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewBaby();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // returning from edit a new baby profile
        if (requestCode == START_BABY_PROF_ACT && resultCode == RESULT_OK) {
            if (data == null) {
                Log.d(TAG, "onActivityResult: START_BABY_PROF_ACT and data == null");
            } else {
                mainBaby = data.getParcelableExtra(BABY_OBJECT_TAG);
                userViewModel.addBaby(userID, mainBaby);
                setUpMainBabyDetails();
            }
        }

        // returning from edit an existing baby profile
        if (requestCode == START_EDIT_BABY_PROF_ACT && resultCode == RESULT_OK) {
            if (data == null) {
                Log.d(TAG, "onActivityResult: START_EDIT_BABY_PROF_ACT and data == null");
            } else {
                activityResultEditBabyProfile(data);
            }
        }

        if (requestCode == START_ADD_BABY_PROF_ACT && resultCode == RESULT_OK) {
            if (data == null) {
                Log.d(TAG, "onActivityResult: START_ADD_BABY_PROF_ACT and data == null");
            } else if (data.getExtras() != null) {

                activityResultAddBabyProfile(data);
            }
        }
    }

    /**
     * This method is called when this activity return with result of edit baby profile
     *
     * @param data
     */
    void activityResultEditBabyProfile(Intent data) {
        mainBaby = data.getParcelableExtra(BABY_OBJECT_TAG);
        userViewModel.updateBaby(mainBaby);
        setUpMainBabyDetails();
    }

    /**
     * This method is called when this activity return with result of add baby profile
     *
     * @param data
     */
    void activityResultAddBabyProfile(Intent data) {
        mainBaby = data.getParcelableExtra(BABY_OBJECT_TAG);
        tempMainBaby = data.getParcelableExtra(OLD_MAIN_BABY_OBJECT_TAG);

        ArrayList<Baby> babiesCopy = new ArrayList<>(userBabies);
        babiesCopy.add(tempMainBaby);
        tempMainBaby = null;
        userViewModel.updateBaby(mainBaby);
        setUpMainBabyDetails();
        userViewModel.updateBabyInCareTaker(userID, mainBaby.getBid());
        userBabies = babiesCopy;
        otherBabiesAdapter.submitList(userBabies);
    }

    /**
     * This method set up the display of the current baby
     */
    private void setUpMainBabyDetails() {
        mainBabyName.setText(mainBaby.getBabyName());
        mainBabyAge.setText(getAgeString(mainBaby.getBabyDOB()));
        Glide.with(this)
                .load(mainBaby.getImageUrl())
                .placeholder(R.mipmap.ic_baby)
                .error(R.mipmap.ic_baby)
                .apply(RequestOptions.circleCropTransform())
                .into(babyImgView);
    }

    /**
     * This method will return the textual age for a given date of birth
     *
     * @param dob date of birth
     * @return age in textual view
     */
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


    // TODO is needed?
    private int valToDp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }


    /**
     * This method will be called when the user would like to create a new baby profile
     */
    private void addNewBaby() {
        babyID = babiesCollection.document().getId();
        mainBaby = new Baby(babyID);
        Intent intent = new Intent(this, BabyProfileActivity.class);
        intent.putExtra(BABY_OBJECT_TAG, mainBaby);

        Log.d(TAG, "starting BabyProfileActivity for result with baby id: " + babyID + " uid: " + userID);
        startActivityForResult(intent, START_BABY_PROF_ACT);
    }

    /**
     * This method will be called when the user want to edit the current baby profile
     */
    private void editCurrentBaby() {
        Intent intent = new Intent(this, BabyProfileActivity.class);
        intent.putExtra(BABY_OBJECT_TAG, mainBaby);

        Log.d(TAG, "starting BabyProfileActivity for result with baby id: " + babyID + " uid: " + userID);
        startActivityForResult(intent, START_EDIT_BABY_PROF_ACT);
    }

    /**
     * This method handle the creation of a new baby profile
     */
    private void createNewBaby() {
        babyID = babiesCollection.document().getId();
        tempMainBaby = mainBaby;
        mainBaby = new Baby(babyID);
        Intent intent = new Intent(this, BabyProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BABY_OBJECT_TAG, mainBaby);
        bundle.putParcelable(OLD_MAIN_BABY_OBJECT_TAG, tempMainBaby);
        intent.putExtras(bundle);
        Log.d(TAG, "starting BabyProfileActivity for result with baby id: " + babyID + " uid: " + userID);
        startActivityForResult(intent, START_ADD_BABY_PROF_ACT);
    }


    @Override
    public void onBabyClick(Baby baby) {
        ArrayList<Baby> babiesCopy = new ArrayList<>(userBabies);
        babiesCopy.remove(baby);
        babiesCopy.add(mainBaby);
        userBabies = babiesCopy;
        mainBaby = baby;
        updateBabiesDetails(userBabies);
        // Todo update menu fragment
        babyID = mainBaby.getBid();
        menuFragment.updateBabyID(babyID);
    }
}
