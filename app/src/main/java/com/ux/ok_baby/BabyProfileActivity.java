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
import android.widget.Toast;

public class BabyProfileActivity extends AppCompatActivity {


    private final int PROFILE_IMG_REQUEST_CODE = 9239;
    private final String TAG = "BabyProfileActivity";

    // Activity main variables
    private ImageView profile_picture;
    private EditText baby_name;
    private EditText baby_dob;
    private Button update_profile_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_profile);

        profile_picture = findViewById(R.id.profile_image);
        baby_name = findViewById(R.id.baby_name);
        baby_dob = findViewById(R.id.dob);
        update_profile_btn = findViewById(R.id.update_profile_btn);


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
//                    viewModel.uploadProfileImageToStorage(MainActivity.getCurrentUserID(), uri).observe(getViewLifecycleOwner(), new Observer<String>() {
//                        @Override
//                        public void onChanged(String storageUri) {
//                            loadImage(storageUri);
//                        }
//                    });
                }
            }
        }
    }

    private void setupUpdateButton() {
        update_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* TODO
                    check whether a profile picture had been chosen
                 */
                if (!setBabyName()) {
                    return;
                }
                if (!setBabyDob()) {
                    return;
                }
                /* TODO
                    send data to firebase for storage.
                    Name + DOB
                    will we send Image only when pressing update
                 */
            }
        });

    }

    private void setupProfileImage() {
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickProfileImage();
            }
        });
    }

    private void loadFromFirebase() {
        /* TODO:
            load existing image from firebase
            load existing name
            load existing d.o.b
         */
    }

    private boolean setBabyName() {
        String baby_name_String = baby_name.getText().toString();
        // Check whether the entered text is not null and not empty
        if (baby_name_String != null && !baby_name_String.isEmpty())
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

    private boolean setBabyDob() {
        String baby_dob_String = baby_dob.getText().toString();
        // Check whether the entered text is not null and not empty
        if (baby_dob_String != null && !baby_dob_String.isEmpty()) {
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
}
