package com.ux.ok_baby.view.ui;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.Baby;
import com.ux.ok_baby.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BabyProfileActivity extends AppCompatActivity {


    private final int PROFILE_IMG_REQUEST_CODE = 9239;
    private final String TAG = "BabyProfileActivity";

    // Activity main variables
    private ImageView profilePicture;
    private EditText babyName;
    private TextView babyDob;
    private Button updateProfileBtn;
//    private String dobString;
    public static Calendar myCalendar;

    // TODO fixed next variables to Model architecture
    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private CollectionReference usersCollection = firestoreDB.collection("users");
    private CollectionReference babiesCollection= firestoreDB.collection("babies");

    User userStub;

    List<DocumentReference> babiesStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_profile);

        profilePicture = findViewById(R.id.profile_image);
        babyName = findViewById(R.id.baby_name);
        babyDob = findViewById(R.id.dob);
        updateProfileBtn = findViewById(R.id.update_profile_btn);
        myCalendar = Calendar.getInstance();

        // TODO remove
        //  User stub

        babiesStub = new ArrayList<>();
//        // babyStub - if want to simulate user with exisiting baby
//        String stubBID = "blasd31";
//        DocumentReference stubRef = babiesCollection.document(stubBID);
//        babiesStub.add(stubRef);

        userStub = new User("S0qjluVcTzToDJVqt8KRm5wu5D52", "s@gmail.com", babiesStub);
        userStub.setBabies(babiesStub);

        // setup views + image.
        setupUpdateButton();
        setupProfileImage();
        loadFromFirestore();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String filePath = data.getData().getPath();
                    Log.d(TAG, "onActivityResult: filepath is " + filePath);
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
                    upload it?
                 */
                if (checkBabyName() && checkBabyDob()) {
                    // checking a baby name and dob are valid

                    String name = babyName.getText().toString();
                    String dob = babyDob.getText().toString();
                    if (userStub.getBabies() == null || userStub.getBabies().isEmpty()) {
                        // user has no babies
                        addNewBabyToDatabase(name, dob);

                    } else {
                        // user has babies - editing an exisiting one
                        String bid = userStub.getBabies().get(0).getId();
                        updateBabyInDatabase(bid, name, dob);
                    }
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

    /**
     * This method will load baby's data from fire base
     */
    private void loadFromFirestore() {
        /* TODO:
            load existing image from firebase
        load existing name
        load existing d.o.b
         */


        // using STUB
        List<DocumentReference> userBabies = userStub.getBabies();
        if ((userBabies != null) && (!userBabies.isEmpty())) {

            // we have data to load from fire store
            DocumentReference babyRef = userBabies.get(0);

            babyRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();

                                if (documentSnapshot.exists()) {

                                    Baby baby = documentSnapshot.toObject(Baby.class);

                                    if (baby.getBabyName() != null && baby.getBabyDOB() != null) {
                                        babyName.setText(baby.getBabyName());
                                        babyDob.setText(baby.getBabyDOB());
                                    }

                                }
                            }
                        }
                    });

        }
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
        babyDob.setText(dateString);
    }


    /**
     * This method will add new baby to fire store
     * @param babyName baby name
     * @param babyDob baby date of birth
     */
    public void addNewBabyToDatabase(String babyName, String babyDob) {

        // Create new baby document to initialize a new random id from fire store
        DocumentReference newBabyRef = babiesCollection.document();
        String bid = newBabyRef.getId();

        // update name and dob in fire store
        updateBabyInDatabase(bid, babyName, babyDob);
    }


    /**
     * This method will update an existing baby in fire store
     * @param bid baby id
     * @param babyName baby name
     * @param babyDob baby date of birth.
     */
    public void updateBabyInDatabase(String bid, String babyName, String babyDob) {
        Baby baby = new Baby(bid, babyName, babyDob);
        DocumentReference babyRef = babiesCollection.document(bid);
        babyRef.set(baby);
    }
}
