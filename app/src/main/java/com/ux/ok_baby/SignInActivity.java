package com.ux.ok_baby;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int MIN_EMAIL_LENGTH = 5; // a@g.c
    private static final int MIN_PASSWORD_LENGTH = 1;
    private static final String VALID_EMAIL_ERROR_MSG = "Please enter a valid email address";
    private static final String VALID_PASSWORD_ERROR_MSG = "Please enter password";
    private EditText signInEmail;
    private EditText signInPassword;
    private Button signInBtn;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        context = this;

        // set up views
        signInEmail = findViewById(R.id.editTextSignUpEmail);
        signInPassword = findViewById(R.id.editTextSignUpPassword);
        signInBtn = findViewById(R.id.buttonSignIn);

        // initialize Firebase auth
        // todo

        // set up ui
        setUpSignInButton();
    }

    private void setUpSignInButton() {
        // If sign in button clicked
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signInEmail.getText().toString();
                String password = signInPassword.getText().toString();

                // validate email and password
                if (email.length() < MIN_EMAIL_LENGTH) {
                    Toast.makeText(context, VALID_EMAIL_ERROR_MSG, Toast.LENGTH_LONG).show();
                }
                if (password.length() < MIN_PASSWORD_LENGTH) {
                    Toast.makeText(context, VALID_PASSWORD_ERROR_MSG, Toast.LENGTH_LONG).show();

                }

                // sign in
                // todo: sign in with email and password
            }
        });


    }
}
