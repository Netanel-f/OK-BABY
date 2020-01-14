package com.ux.ok_baby.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ux.ok_baby.model.Baby;
import com.ux.ok_baby.model.User;

public class FirestoreRepository {

    private static final String TAG = "FirestoreRepository";

    private FirebaseFirestore firestoreDB;
    private MutableLiveData<User> curUser;
    private MutableLiveData<Baby> curBaby;
    private CollectionReference usersCollection = firestoreDB.collection("users");
    private CollectionReference babiesCollection = firestoreDB.collection("babies");

    public FirestoreRepository() {
        this.firestoreDB = FirebaseFirestore.getInstance();
        this.curUser = new MutableLiveData<>();
        this.curBaby = new MutableLiveData<>();
    }

    /* BABY */

    /**
     * Add baby to database and to update its caretaker user in the database.
     * @param caretakerUid - UID of the user the baby is added to.
     * @param baby         - baby to add.
     */
    public void addBaby(String caretakerUid, Baby baby) {
        DocumentReference babyRef;
        String bid;
        if (baby == null) {
            baby = new Baby();
        }
        if (baby.getBid() == null || baby.getBid().isEmpty()) {
            // generate baby id from firebase
            babyRef = babiesCollection.document();
            bid = babyRef.getId();

            // update baby object
            baby.setBid(bid);
        }

        // update baby in db
        updateBaby(baby);

        // update caretaker
        addBabyToUser(caretakerUid, baby.getBid());

    }


    /**
     * Update the baby in the database by the baby's bid.
     * @param baby - baby to update.
     */
    public void updateBaby(Baby baby) {
        DocumentReference babyRef = babiesCollection.document(baby.getBid());
        babyRef.set(baby);
    }

    /**
     * Update specific field of the baby.
     * @param bid - id of the baby to update.
     * @param field - field to update.
     * @param value - value to update.
     */
    public void updateBaby(String bid, String field, Object value) {
        DocumentReference babyRef = babiesCollection.document(bid);
        babyRef
                .update(field, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Baby successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating baby", e);
                    }
                });
    }

    /**
     * Get baby from database.
     *
     * @param bid - id of the baby to get.
     * @return observable LiveData baby object from db.
     */
    public LiveData<Baby> getBaby(String bid) {
        DocumentReference babyRef = babiesCollection.document(bid);
        babyRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Baby baby = documentSnapshot.toObject(Baby.class);
                                curBaby.postValue(baby);
                            }
                        }
                    }
                });
        return curBaby;
    }

    /* USER */

    /**
     * Update user in db by uid.
     * @param user - user to update.
     */
    public void updateUser(User user) {
        DocumentReference userRef = babiesCollection.document(user.getUid());
        userRef.set(user);
    }

    /**
     * Update specific field of user in db by uid.
     * @param uid - id of the user to update.
     * @param field - field of the user to update.
     * @param value - value to update.
     */
    public void updateUser(String uid, String field, Object value) {
        DocumentReference userRef = usersCollection.document(uid);
        userRef
                .update(field, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating user", e);
                    }
                });
    }

    /**
     * Get user from database.
     * @param uid - id of the user to update.
     * @return observable LiveData user object from db.
     */
    public LiveData<User> getUser(String uid) {
        DocumentReference userRef = usersCollection.document(uid);
        userRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                curUser.postValue(user);
                            }
                        }
                    }
                });
        return curUser;
    }

    /**
     * Add baby (by bid) to user in the database.
     * @param caretakerUID - uid of the user.
     * @param bid - uid of the baby.
     */
    public void addBabyToUser(String caretakerUID, String bid) {
        // create references
        DocumentReference userRef = usersCollection.document(caretakerUID);
        DocumentReference babyRef = babiesCollection.document(bid);

        // update user
        userRef.update("babies", FieldValue.arrayUnion(babyRef));
    }

}
