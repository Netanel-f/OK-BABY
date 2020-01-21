package com.ux.ok_baby.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.Baby;
import com.ux.ok_baby.model.User;
import com.ux.ok_baby.viewmodel.UserViewModel;

import java.util.ArrayList;

import com.ux.ok_baby.utils.Constants;

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

    private UserViewModel viewModel;

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

        // initialize Firebase
        auth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        checkIfLoggedIn(auth.getCurrentUser());

        setUpUI();
    }

    private void setUpUI(){
        setUpSignInButton();
        setUpSignUpButton();
    }

    private void checkIfLoggedIn(FirebaseUser authCurrentUser){
        if (authCurrentUser != null){
            getUserFromDatabase(authCurrentUser);
        }
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
            Log.w(TAG, "signInToFirebase: Attempthing to authenticate user");
            authenticateUser(email, password);
    }

    private void authenticateUser(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            Log.w(TAG, "onComplete: succussfully logged in "+user.getUid());
//                            createUser(user);
                            getUserFromDatabase(user);
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
//                            createUser(user);
                            getUserFromDatabase(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            showProgressBar(false);
                        }
                    }
                });
    }


    /**
     * Navigates to the next activity according to the user's status: new user or existing user.
     * @param authUser - FirebaseUser
     */
    private void getUserFromDatabase(FirebaseUser authUser) {
        final String uid = authUser.getUid();
        final String email = authUser.getEmail();
        final LifecycleOwner lifecycleOwner = this;
        viewModel.getUser(uid).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // got user
                if (user != null) {
                    Log.w(TAG, "onChanged: observed user "+user.getUid());
                    if (user.getBabies() == null || user.getBabies().isEmpty()) {
                        // baby list is empty -> treat like new user
                        Log.w(TAG, "user has no babies");
                        navigateToNextActivity(uid, true);
//                        navigateToNextActivity(true);
                    } else if (user.getBabies().get(0) != null) {
                        final DocumentReference babyRef = user.getBabies().get(0);
                        viewModel.getBaby(user.getBabies().get(0).getId()).observe(lifecycleOwner, new Observer<Baby>() {
                            @Override
                            public void onChanged(Baby baby) {
                                if (baby.getBabyName() == null || baby.getBabyDOB() == null) {
//                                    navigateToNextActivity(uid, babyRef, true);
                                    navigateToNextActivity(uid, true);
                                } else {
                                    navigateToNextActivity(uid, babyRef, false);
//                                    navigateToNextActivity(uid, false);
                                }
                            }
                        });

                    } else {
                        Log.d(TAG, "user has babies");
                        navigateToNextActivity(uid, user.getBabies().get(0), false);
//                        navigateToNextActivity(uid, false);
//                        navigateToNextActivity(false);
                    }
                }
                else {
                    Log.w(TAG, "User doesn't exist in database. UID: " + uid + " email: " +email);
                    addNewUser(uid, email);//todo handle this flow
                }
            }
        });
    }

    private void addNewUser(final String uid, String email) {
        addUserToDatabase(uid, email);
//        viewModel.getUser(uid).observe(this, new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                navigateToNextActivity(uid, user.getBabies().get(0), true);
//            }
//        });
        navigateToNextActivity(uid, true);
//        navigateToNextActivity(true);
    }



    private void navigateToNextActivity(String uid, Boolean isNewUser) {
        Intent homeIntent = new Intent(this, HomeFragment.class);

        homeIntent.putExtra(Constants.USER_ID_TAG, uid);
        homeIntent.putExtra(Constants.IS_NEW_USER_TAG, isNewUser);
        startActivity(homeIntent);
    }

    private void navigateToNextActivity(String uid, DocumentReference babyRef, Boolean isNewUser) {
        Intent homeIntent = new Intent(this, HomeFragment.class);

        homeIntent.putExtra(Constants.USER_ID_TAG, uid);
        homeIntent.putExtra(Constants.IS_NEW_USER_TAG, isNewUser);
        homeIntent.putExtra(Constants.BABY_ID, babyRef.getId());
        startActivity(homeIntent);
    }


    /**
     * Determines if the user is a new user or an existing user and navigates accordingly.
     * @param isNewUser - true if the user is new or has no babies.
     */
    private void navigateToNextActivity(Boolean isNewUser){
        // todo delete
        if (isNewUser) {
            newUserNavigation();
        } else {
            existingUserNavigation();
        }
    }


    /**
     * Navigates to the screen a new user should go to: AddBabyActivity.
     */
    private void newUserNavigation() {
        // todo delete
        Intent addBabyIntent = new Intent(this, BabyProfileActivity.class);
        startActivity(addBabyIntent);

    }

    /**
     * Navigates to the screen an existing user should go to: HomeFragment.
     */
    private void existingUserNavigation() {
        // todo delete
        Intent homeIntent = new Intent(this, HomeFragment.class);
        startActivity(homeIntent);
    }


    /**
     * DATABASE FUNCTIONS
     */

    public void addUserToDatabase(String uid, String email) {
        User user = new User(uid, email, new ArrayList<DocumentReference>());
        viewModel.addNewUser(user);
    }


}
