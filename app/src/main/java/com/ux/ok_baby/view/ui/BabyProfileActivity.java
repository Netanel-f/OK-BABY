package com.ux.ok_baby.view.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.ux.ok_baby.R;
import com.ux.ok_baby.Model.Baby;
import com.ux.ok_baby.Model.User;
import com.ux.ok_baby.view.popups.DateTimePicker;
import com.ux.ok_baby.view.ui.HomeFragment;
import com.ux.ok_baby.utils.Constants;

import java.util.Calendar;
import java.util.List;

import static com.ux.ok_baby.utils.Constants.BABY_ID;
import static com.ux.ok_baby.utils.Constants.BABY_OBJECT_TAG;

public class BabyProfileActivity extends AppCompatActivity {


    private final int PROFILE_IMG_REQUEST_CODE = 9239;
    private final String ACTIVITY_TAG = "BabyProfileActivity";

    // Activity main variables
    private ImageView profilePicture;
    private EditText babyName;
    private TextView babyDob;
    private Button updateProfileBtn;
    public static Calendar myCalendar;
    private Context context;

    private Baby babyProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_profile);


        // set views
        context = this;
        profilePicture = findViewById(R.id.profile_image);
        babyName = findViewById(R.id.baby_name);
        babyDob = findViewById(R.id.dob);
        updateProfileBtn = findViewById(R.id.update_profile_btn);


        // get calendar instance for date picker
        myCalendar = Calendar.getInstance();

        // get Baby object for editing
        babyProfile = getIntent().getParcelableExtra(BABY_OBJECT_TAG);

        // setup views + image.
        setupUpdateButton();
        setupProfileImage();
        loadFromBabyObject();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    if (uri != null) {
                        String filePath = uri.getPath();

                        Log.d(ACTIVITY_TAG, "onActivityResult: filepath is " + filePath);
                        Log.d(ACTIVITY_TAG, "Uri: " + uri.toString());

                        // upload image to storage
                        uploadProfileImageToStorage(uri);
                    }
                }
            }
        }
    }

    /**
     * This method will setup the functionality of the update profile button
     */
    private void setupUpdateButton() {
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBabyName() && checkBabyDob()) {
                    // checking a baby name and dob are valid
                    babyProfile.setBabyName(babyName.getText().toString());
                    babyProfile.setBabyDOB(babyDob.getText().toString());

                    // TODO return to last activity ?

                    Intent intent = new Intent(context, HomeFragment.class);
                    intent.putExtra(BABY_OBJECT_TAG, babyProfile);
                    startActivity(intent);
                }

            }
        });

    }


    /**
     * This method will setup the functionality of choosing image
     */
    private void setupProfileImage() {
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickProfileImage();
            }
        });
    }


    /**
     * This method will load image from storage to the place hodler
     * @param image_uri
     */
    private void loadImage(String image_uri) {
        Glide.with(this)
                .load(image_uri).placeholder(R.drawable.ic_default_user_profile_image)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);
    }


    /**
     * load the data from Baby object to present on UI
     */
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

    /**
     * This method will open an explorer to choose image from device storage
     */
    private void pickProfileImage() {
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
         startActivityForResult(myIntent, PROFILE_IMG_REQUEST_CODE);
    }


    /**
     * This method will upload a select profile image to Storage
     * @param localUri
     */
    private void uploadProfileImageToStorage(final Uri localUri) {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference(babyProfile.getBid())
                .child("profile-image");

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
        DateTimePicker dateTimePicker = new DateTimePicker(context);
        dateTimePicker.datePicker(babyDob);
    }

    /**
     * This method will save the date that has been picked into the profile.
     * This method will be called only from the DialogFragment class.
     * @param dateString D.O.B string to save
     */
    public void processDatePickerResult(String dateString) {
        babyDob.setText(dateString);
    }
}
