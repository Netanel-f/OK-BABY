package com.ux.ok_baby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int MIN_EMAIL_LENGTH = 5; // a@g.c
    private static final int MIN_PASSWORD_LENGTH = 1;
    private static final String VALID_EMAIL_ERROR_MSG = "Please enter a valid email address";
    private static final String VALID_PASSWORD_ERROR_MSG = "Please enter password";

    private EditText signInEmail;
    private EditText signInPassword;
    private Button signInBtn;
    private ProgressBar progressBar;
    private TextView signUpLink;

    private Context context;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        context = this;

        // set up views
        signInEmail = findViewById(R.id.editTextSignUpEmail);
        signInPassword = findViewById(R.id.editTextSignUpPassword);
        signInBtn = findViewById(R.id.buttonSignIn);
        progressBar = findViewById(R.id.progressBarSignIn);
        signUpLink = findViewById(R.id.signUpLink);

        // initialize Firebase auth
        auth = FirebaseAuth.getInstance();

        setUpUI();
    }

    private void setUpUI(){
        setUpSignInButton();
        setUpSignUpButton();
    }

    /**
     * Sets up the sign in button.
     */
    private void setUpSignInButton() {
        // If sign in button clicked
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sign in
                String email = signInEmail.getText().toString();
                String password = signInPassword.getText().toString();
                signIn(email, password);
            }
        });
    }

    /**
     * Sets up the log in button.
     */
    private void setUpSignUpButton() {
        // If sign in button clicked
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sign in
                String email = signInEmail.getText().toString();
                String password = signInPassword.getText().toString();
                signUp(email, password);
            }
        });
    }

    /**
     * Starts the sign in process for the given email and password.
     * @param email - entered email (string).
     * @param password - entered password (string).
     */
    private void signIn(String email, String password) {
        if (!validateEmailAndPassword(email, password)) {
            return;
        }

        showProgressBar(true);
        signInToFirebase(email, password);
    }

    /**
     * Starts the sign in process for the given email and password.
     * @param email - entered email (string).
     * @param password - entered password (string).
     */
    private void signUp(String email, String password) {
        if (!validateEmailAndPassword(email, password)) {
            return;
        }

        showProgressBar(true);
        createAuthenticationUser(email, password);
    }


    /**
     * Validates that the email and password are of reasonable length.
     * If not, displays a Toast with the appropriate error.
     * @param email - entered email (string).
     * @param password - entered password (string).
     * @return true if the email and password were validated, false otherwise.
     */
    private boolean validateEmailAndPassword(String email, String password) {
        boolean valid = true;

        if (email.length() < MIN_EMAIL_LENGTH) {
            Toast.makeText(context, VALID_EMAIL_ERROR_MSG, Toast.LENGTH_LONG).show();
            valid = false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            Toast.makeText(context, VALID_PASSWORD_ERROR_MSG, Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }

    /**
     * Sets the visibility of the progress bar.
     * @param show - if true, shows the progress bar. Otherwise, hides it.
     */
    private void showProgressBar(Boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Authenticates the email and password against Firebase Authentication.
     * If the user doesn't exist, creates it.
     * @param email
     * @param password
     */
    private void signInToFirebase(String email, String password) {
        // todo: get if exists, create with auth user uid if doesn't
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            authenticateUser(email, password);
        } else {
            navigateToNextActivity(currentUser);
        }
    }

    private void authenticateUser(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            createUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            showProgressBar(false);
                        }
                    }
                });
    }

    private void createAuthenticationUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            createUser(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            showProgressBar(false);
                        }
                    }
                });
    }

    private void createUser(FirebaseUser user){
        // todo: add user to db
        // check if existing
        navigateToNextActivity(user);
    }


    /**
     * Navigates to the next activity according to the user's status: new user or existing user.
     * @param user - FirebaseUser
     */
    private void navigateToNextActivity(FirebaseUser user) {
        if (isNewUser(user)) {
            newUserNavigation();
        } else {
            existingUserNavigation();
        }
    }

    /**
     * Determines if the user is a new user or an existing user.
     * @param user - the user to determine.
     * @return True if a new user, false otherwise.
     */
    private boolean isNewUser(FirebaseUser user) {
        // todo
        return true;
    }

    /**
     * Navigates to the screen a new user should go to: AddBabyActivity.
     */
    private void newUserNavigation() {
        Intent addBabyIntent = new Intent(this, AddBabyActivity.class);
        // todo: firebase user
        startActivity(addBabyIntent);

    }

    /**
     * Navigates to the screen an existing user should go to: HomeFragment.
     */
    private void existingUserNavigation() {
        Intent homeIntent = new Intent(this, HomeFragment.class);
        // todo: firebase user
        startActivity(homeIntent);
    }
}
