package com.ux.ok_baby;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ux.ok_baby.Model.Baby;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ux.ok_baby.Constants.BABY_ID;
import static com.ux.ok_baby.Constants.DATE_PATTERN;

/**
 * First screen when loading the app (after sign in).
 * Contains buttons to navigate to other screens.
 */
public class HomeFragment extends FragmentActivity {
    private final String TAG = "HomeFragment";
    private String babyID, babyDOB,babyName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        // TODO: 1/4/2020 get baby details from parent activity.

        babyID  = getIntent().getStringExtra(BABY_ID);
        babyID = "69kkdHZH48TOYdXWq1hP";
//        setUpBabyDetails();
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
        getBabyNameNAge();
        ((TextView)findViewById(R.id.babyName)).setText(babyName);
        ((TextView)findViewById(R.id.babyAge)).setText(getAge(babyDOB));

    }

    private void getBabyNameNAge() {
        CollectionReference babiesCollection = FirebaseFirestore.getInstance().collection("babies");
        DocumentReference babyRef = babiesCollection.document(babyID);
        // TODO: 1/12/2020 what is wrong?
        babiesCollection.document(babyID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                babyDOB = documentSnapshot.getString("babyDOB");
                babyName = documentSnapshot.getString("babyName");
            }
        });
    }

    private int getAge(String dobString){
    //TODO change to string
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month+1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }



        return age;
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
