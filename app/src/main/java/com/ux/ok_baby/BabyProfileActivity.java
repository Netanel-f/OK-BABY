package com.ux.ok_baby;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class BabyProfileActivity extends AppCompatActivity {

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
                    Name + DOB + IMAGE
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
        if (baby_name.getText().toString() != null)//check whether the entered text is not null
        {
            //display the text that you entered in edit text
            Toast.makeText(getApplicationContext(), "Enter a baby name", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean setBabyDob() {
        if (baby_dob.getText().toString() != null) {
            Toast.makeText(getApplicationContext(), "Enter Valid date of birth", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void pickProfileImage() {
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
        /* TODO:
            Start Activity for result
            choose image from device
            upload to Firebase
            return to activity
         */
        // startActivityForResult(myIntent, PROFILE_IMG_REQUEST_CODE);
    }
}
