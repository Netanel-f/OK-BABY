package com.ux.ok_baby.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import java.util.List;

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
    private ConstraintLayout splashScreenLayout;

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
        splashScreenLayout = findViewById(R.id.splashScreen);

        // initialize Firebase
        auth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        checkIfLoggedIn(auth.getCurrentUser());

        setUpUI();
    }


    /**
     * Set up UI functionality
     */
    private void setUpUI(){
        setUpSignInButton();
        setUpSignUpButton();
    }


    /**
     * check if the current authenticated user is logged in
     * @param authCurrentUser FirebaseUser token
     */
    private void checkIfLoggedIn(FirebaseUser authCurrentUser){
        if (authCurrentUser != null){
            getUserFromDatabase(authCurrentUser);
        } else {
            // user need to register or sign in, reveal sign in activity
            splashScreenLayout.setVisibility(View.INVISIBLE);
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
     * @param email user email address
     * @param password user password
     */
    private void signInToFirebase(String email, String password) {
            Log.w(TAG, "signInToFirebase: Attempting to authenticate user");
            authenticateUser(email, password);
    }


    /**
     * Authenticates the email and password against Firebase Authentication.
     * @param email user email address
     * @param password user password
     */
    private void authenticateUser(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            Log.w(TAG, "onComplete: successfully logged in "+user.getUid());
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
     * Method for creating authentication user
     * @param email user email address
     * @param password user password
     */
    private void createAuthenticationUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
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
            public void onChanged(final User user) {
                // got user
                if (user != null) {
                    Log.w(TAG, "onChanged: observed user "+user.getUid());
                    final List<DocumentReference> userBabies = user.getBabies();
                    getUserBabiesFromDatabase(uid, user, userBabies, lifecycleOwner);

                } else {
                    Log.w(TAG, "User doesn't exist in database. UID: " + uid + " email: " +email);
                    addNewUser(uid, email);
                }
            }
        });
    }


    /**
     * This method will get the babies information for a specific user
     * @param uid user id
     * @param user User object to save data into
     * @param userBabies List of Document References of user babies
     * @param lifecycleOwner owner of current life cycle
     */
    private void getUserBabiesFromDatabase(final String uid, User user, final List<DocumentReference> userBabies, final LifecycleOwner lifecycleOwner) {
        if (userBabies == null || userBabies.isEmpty()) {
            // baby list is empty -> treat like new user
            Log.w(TAG, "user has no babies");
            navigateToNextActivity(uid, true);

        } else if (userBabies.get(0) != null) {
            final DocumentReference babyRef = user.getBabies().get(0);
            viewModel.getBaby(user.getBabies().get(0).getId()).observe(lifecycleOwner, new Observer<Baby>() {
                @Override
                public void onChanged(Baby baby) {
                    if (baby.getBabyName() == null || baby.getBabyDOB() == null) {
                        navigateToNextActivity(uid, true);

                    } else {
                        // get other user babies
                        navigateToNextActivity(uid, babyRef, false);
                    }
                }
            });

        } else {
            Log.d(TAG, "user has babies");
            navigateToNextActivity(uid, user.getBabies().get(0), false);
        }
    }


    /**
     * add new user
     * @param uid user id
     * @param email user email
     */
    private void addNewUser(final String uid, String email) {
        addUserToDatabase(uid, email);
        navigateToNextActivity(uid, true);
    }


    /**
     * Determines if the user is a new user or an existing user and navigates accordingly.
     * @param uid - user id
     * @param isNewUser  - true if the user is new or has no babies.
     */
    private void navigateToNextActivity(String uid, boolean isNewUser) {
        Intent homeIntent = new Intent(this, HomeFragment.class);

        homeIntent.putExtra(Constants.USER_ID_TAG, uid);
        homeIntent.putExtra(Constants.IS_NEW_USER_TAG, isNewUser);
        startActivity(homeIntent);
    }


    private void navigateToNextActivity(String uid, DocumentReference babyRef, boolean isNewUser) {
        Intent homeIntent = new Intent(this, HomeFragment.class);

        homeIntent.putExtra(Constants.USER_ID_TAG, uid);
        homeIntent.putExtra(Constants.IS_NEW_USER_TAG, isNewUser);
        homeIntent.putExtra(Constants.BABY_ID, babyRef.getId());
        startActivity(homeIntent);
    }


    /**
     * add user to data base
     * @param uid user id
     * @param email user email
     */
    public void addUserToDatabase(String uid, String email) {
        User user = new User(uid, email, new ArrayList<DocumentReference>());
        viewModel.addNewUser(user);
    }
}
