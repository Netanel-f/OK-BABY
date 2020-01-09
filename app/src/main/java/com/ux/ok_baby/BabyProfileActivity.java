package com.ux.ok_baby;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;

public class BabyProfileActivity extends AppCompatActivity {


    private final int PROFILE_IMG_REQUEST_CODE = 9239;
    private final String TAG = "BabyProfileActivity";

    // Activity main variables
    private ImageView profilePicture;
    private EditText babyName;
    private TextView babyDob;
    private Button updateProfileBtn;
    private String dobString;
    public static Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_profile);

        profilePicture = findViewById(R.id.profile_image);
        babyName = findViewById(R.id.baby_name);
        babyDob = findViewById(R.id.dob);
        updateProfileBtn = findViewById(R.id.update_profile_btn);
        myCalendar = Calendar.getInstance();
        // setup views + image.
        setupUpdateButton();
        setupProfileImage();
        loadFromFirebase();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String filePath = data.getData().getPath();
                    android.util.Log.d(TAG, "onActivityResult: filepath is " + filePath);
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());
                    /* TODO:
                           The next code is needed if we want immediate upload of img to firebase
                    */
                }
            }
        }
    }

    private void setupUpdateButton() {
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* TODO
                    check whether a profile picture had been chosen
                 */
                if (checkBabyName() && checkBabyDob()) {
                    /* TODO
                        send data to firebase for storage.
                        Name + DOB
                        will we send Image only when pressing update
                    */
                }

            }
        });

    }

    private void setupProfileImage() {
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickProfileImage();
            }
        });
    }

    private void loadImage(String image_uri) {
        Glide.with(this)
                .load(image_uri).placeholder(R.drawable.ic_default_user_profile_image)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);
    }

    private void loadFromFirebase() {
        /* TODO:
            load existing image from firebase
            load existing name
            load existing d.o.b
         */
    }

    /**
     * check that the user has typed a name for the baby
     * @return True iff babyName is not null && not empty, false otherwise.
     */
    private boolean checkBabyName() {
        String babyNameString = babyName.getText().toString();
        // Check whether the entered text is not null and not empty
        if (babyNameString != null && !babyNameString.isEmpty())
        {
            //display the text that you entered in edit text
            /* TODO:
                update Firebase with new baby name
             */
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "Enter a baby name", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * check that the user has typed a dob for the baby
     * @return True iff baby dob is not empty, false otherwise.
     */
    private boolean checkBabyDob() {
        String babyDobString = babyDob.getText().toString();
        // Check whether the entered text is not null and not empty
        if (babyDobString != null && !babyDobString.isEmpty()) {
            /* TODO:
                update Firebase with new baby DOB
             */
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "Enter Valid date of birth", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void pickProfileImage() {
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
        /* TODO:
            upload to Firebase
            return to activity
         */
         startActivityForResult(myIntent, PROFILE_IMG_REQUEST_CODE);
    }

    /**
     * This method will initiate a date Picker Dialog fragment
     * @param view current view.
     */
    public void showDatePickerDialog(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * This method will save the date that has been picked into the profile.
     * This method will be called only from the DialogFragment class.
     * @param dateString D.O.B string to save
     */
    public void processDatePickerResult(String dateString) {
        dobString = dateString;
        babyDob.setText(dobString);
    }
}
