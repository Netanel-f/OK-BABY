package com.ux.ok_baby.view.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ux.ok_baby.Constants.BABY_ID;
import static com.ux.ok_baby.Constants.DATE_PATTERN;

/**
 * First screen when loading the app (after sign in).
 * Contains buttons to navigate to other screens.
 */
public class HomeFragment extends FragmentActivity {
    private final String TAG = "HomeFragment";
    private String babyID, babyDOB, babyName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        // TODO: 1/4/2020 get baby details from parent activity.

        babyID = getIntent().getStringExtra(BABY_ID);
        babyID = "69kkdHZH48TOYdXWq1hP";
        setUpBabyDetails();
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            MenuFragment menuFragment = new MenuFragment(babyID);
//            menuFragment.setArguments(intent.getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment).commit();
        }
        setUpOtherBabies();
    }


    private void setUpBabyDetails() {
        CollectionReference babiesCollection = FirebaseFirestore.getInstance().collection("babies");
        DocumentReference babyRef = babiesCollection.document(babyID);
        babyRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ((TextView) findViewById(R.id.babyName)).setText(documentSnapshot.getString("babyName"));
                ((TextView) findViewById(R.id.babyAge)).setText(getAgeByMonths(documentSnapshot.getString("babyDOB")) + " months old");
            }
        });
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
}
