package com.ux.ok_baby;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.ux.ok_baby.Model.Baby;

import java.util.Calendar;

import static com.ux.ok_baby.Constants.BABY_OBJECT_TAG;

public class BabyProfileActivity extends AppCompatActivity {


    private final int PROFILE_IMG_REQUEST_CODE = 9239;
    private final String ACTIVITY_TAG = "BabyProfileActivity";

    // Activity main variables
    private ImageView profilePicture;
    private EditText babyName;
    private TextView babyDob;
    private Button updateProfileBtn;
//    private String dobString;
    public static Calendar myCalendar;

    // TODO fixed next variables to Model architecture
    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
//    private CollectionReference usersCollection = firestoreDB.collection("users");
//    private CollectionReference babiesCollection= firestoreDB.collection("babies");

    private Baby babyProfile;
//    User userStub;
//    List<DocumentReference> babiesStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_profile);


        // set views
        profilePicture = findViewById(R.id.profile_image);
        babyName = findViewById(R.id.baby_name);
        babyDob = findViewById(R.id.dob);
        updateProfileBtn = findViewById(R.id.update_profile_btn);

        // get calendar instance for date picker
        myCalendar = Calendar.getInstance();

        // get Baby object for editing
        babyProfile = getIntent().getParcelableExtra(BABY_OBJECT_TAG);


        // TODO REMOVE - fake babyprofile
        babyProfile = new Baby();
        babyProfile.setBid("IQHnYPty1MC77Xd8H2u9");
        babyProfile.setBabyName("Tutit");
        babyProfile.setBabyDOB("01/12/2019");

//        // TODO remove
//        //  User stub
//
//        babiesStub = new ArrayList<>();
////        // babyStub - if want to simulate user with existing baby
////        String stubBID = "blasd31";
////        DocumentReference stubRef = babiesCollection.document(stubBID);
////        babiesStub.add(stubRef);
//
//        userStub = new User("S0qjluVcTzToDJVqt8KRm5wu5D52", "s@gmail.com", babiesStub);
//        userStub.setBabies(babiesStub);

        // setup views + image.
        setupUpdateButton();
        setupProfileImage();
        loadFromBabyObject();
//        loadFromFirestore();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String filePath = data.getData().getPath();
                    Log.d(ACTIVITY_TAG, "onActivityResult: filepath is " + filePath);
                    final Uri uri = data.getData();
                    Log.d(ACTIVITY_TAG, "Uri: " + uri.toString());
                    /* TODO:
                           The next code is needed if we want immediate upload of img to firebase
                    */
                    uploadProfileImageToStorage(uri);
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

                    babyProfile.setBabyName(babyName.getText().toString());
                    babyProfile.setBabyDOB(babyDob.getText().toString());
                    // TODO return to last activity

//                    String name = babyName.getText().toString();
//                    String dob = babyDob.getText().toString();

//                    if (userStub.getBabies() == null || userStub.getBabies().isEmpty()) {
//                        // user has no babies
//                        addNewBabyToDatabase(name, dob);
//
//                    } else {
//                        // user has babies - editing an exisiting one
//                        String bid = userStub.getBabies().get(0).getId();
//                        updateBabyInDatabase(bid, name, dob);
//                    }
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

//    /**
//     * This method will load baby's data from fire base
//     */
//    private void loadFromFirestore() {
//        // TODO: migrate this function to ViewModel
//        /* TODO:
//            load existing image from firebase
//        load existing name
//        load existing d.o.b
//         */
//
//
//        // using STUB
//        List<DocumentReference> userBabies = userStub.getBabies();
//        if ((userBabies != null) && (!userBabies.isEmpty())) {
//
//            // we have data to load from fire store
//            DocumentReference babyRef = userBabies.get(0);
//
//            babyRef.get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                DocumentSnapshot documentSnapshot = task.getResult();
//
//                                if (documentSnapshot.exists()) {
//
//                                    Baby baby = documentSnapshot.toObject(Baby.class);
//
//                                    if (baby.getBabyName() != null && baby.getBabyDOB() != null) {
//                                        babyName.setText(baby.getBabyName());
//                                        babyDob.setText(baby.getBabyDOB());
//                                    }
//
//                                }
//                            }
//                        }
//                    });
//
//        }
//    }


    private void loadFromBabyObject() {
        String babyNameString = babyProfile.getBabyName();
        if (babyNameString != null && !babyNameString.isEmpty()) {
            babyName.setText(babyNameString);
        }

        String babyDobString = babyProfile.getBabyDOB();
        if (babyDobString != null && !babyDobString.isEmpty()) {
            babyDob.setText(babyDobString);
        }

        String imgUrl = babyProfile.getImageUrl();
        if (imgUrl != null && !imgUrl.equals("")) {
            loadImage(imgUrl);
        }
    }

    /**
     * check that the user has typed a name for the baby
     * @return True iff babyName is not null && not empty, false otherwise.
     */
    private boolean checkBabyName() {
        String babyNameString = babyName.getText().toString();
        // Check whether the entered text is not null and not empty
        if (babyNameString != null && !babyNameString.isEmpty()) {
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


    private void uploadProfileImageToStorage(final Uri localUri) {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference(babyProfile.getBid())
                .child("profile-image");
//                .child(localUri.getLastPathSegment());

        UploadTask uploadTask = storageReference.putFile(localUri);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getMetadata().getReference().getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        // update the profile to use the image
                                        String url = task.getResult().toString();

                                        // save it to baby object
                                        babyProfile.setImageUrl(url);
                                        loadImage(url);
                                    }
                                }
                            });
                } else {
                    Log.d(ACTIVITY_TAG, "Image upload task failed: " + task.getException());
                }
            }
        });
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


//    /**
//     * This method will add new baby to fire store
//     * @param babyName baby name
//     * @param babyDob baby date of birth
//     */
//    public void addNewBabyToDatabase(String babyName, String babyDob) {
//        // TODO: delete candidate
//        // Create new baby document to initialize a new random id from fire store
//        DocumentReference newBabyRef = babiesCollection.document();
//        String bid = newBabyRef.getId();
//
//        // update name and dob in fire store
//        updateBabyInDatabase(bid, babyName, babyDob);
//    }
//
//
//    /**
//     * This method will update an existing baby in fire store
//     * @param bid baby id
//     * @param babyName baby name
//     * @param babyDob baby date of birth.
//     */
//    public void updateBabyInDatabase(String bid, String babyName, String babyDob) {
//        // TODO: migrate this function to ViewModel
//        Baby baby = new Baby(bid, babyName, babyDob);
//        DocumentReference babyRef = babiesCollection.document(bid);
//        babyRef.set(baby);
//    }
}
