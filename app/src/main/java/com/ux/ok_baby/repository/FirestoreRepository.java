package com.ux.ok_baby.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ux.ok_baby.model.Baby;
import com.ux.ok_baby.model.DiaperEntry;
import com.ux.ok_baby.model.FoodEntry;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.model.User;
import com.ux.ok_baby.model.SleepEntry;
import com.ux.ok_baby.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreRepository {

    private static final String TAG = "FirestoreRepository";

    private FirebaseFirestore firestoreDB;
    private MutableLiveData<User> curUser;
    private MutableLiveData<Baby> curBaby;
    private MutableLiveData<List<Baby>> userBabies;

    private MutableLiveData<List<ReportEntry>> sleepEntries;
    private MutableLiveData<List<ReportEntry>> diaperEntries;
    private MutableLiveData<List<ReportEntry>> foodEntries;
//    private MutableLiveData<List<ReportEntry>> curEntries;
    private CollectionReference usersCollection;
    private CollectionReference babiesCollection;

    private static final Map<Constants.ReportType, String> reportToCollection = createMap();

    public FirestoreRepository() {
        this.firestoreDB = FirebaseFirestore.getInstance();
        this.usersCollection = firestoreDB.collection("users");
        this.babiesCollection = firestoreDB.collection("babies");
        this.curUser = new MutableLiveData<>();
        this.curBaby = new MutableLiveData<>();
        this.userBabies =  new MutableLiveData<>();
        this.sleepEntries = new MutableLiveData<>();
        this.diaperEntries = new MutableLiveData<>();
        this.foodEntries = new MutableLiveData<>();
    }

    private static Map<Constants.ReportType, String> createMap() {
        Map<Constants.ReportType,String> myMap = new HashMap<>();
        myMap.put(Constants.ReportType.SLEEP, "sleep_reports");
        myMap.put(Constants.ReportType.FOOD, "food_reports");
        myMap.put(Constants.ReportType.DIAPER, "diaper_reports");
        myMap.put(Constants.ReportType.OTHER, "other_reports");
        return myMap;
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

    public LiveData<List<Baby>> getUserBabies(String uid) {

        final List<Baby> listBabies = new ArrayList<>();
        DocumentReference docRef = firestoreDB.collection("users").document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<DocumentReference> list = (List<DocumentReference>) document.get("babies");
                        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                        for (DocumentReference documentReference : list) {
                            Task<DocumentSnapshot> documentSnapshotTask = documentReference.get();
                            tasks.add(documentSnapshotTask);
                        }
                        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> list) {
                                //Do what you need to do with your list
                                for (Object object : list) {
                                    Baby baby = ((DocumentSnapshot) object).toObject(Baby.class);
                                    listBabies.add(baby);
                                    Log.d("TAG", "baby id: " + baby.getBid());
                                }
                                userBabies.postValue(listBabies);
                            }
                        });
                    }
                }
            }
        });
        return userBabies;
    }

    /* USER */

    /**
     * Update user in db by uid.
     * @param user - user to update.
     */
    public void updateUser(User user) {
        DocumentReference userRef = usersCollection.document(user.getUid());
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
                            } else {
                                curUser.postValue(null);
                            }
                        } else {
                            curUser.postValue(null);
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


//    public LiveData<List<SleepEntry>> getSleepEntries(String bid){
//        babiesCollection.document(bid)
//                .collection("sleep_reports")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    List<SleepEntry> entries = new ArrayList<>();
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            SleepEntry entry = document.toObject(SleepEntry.class);
//                            entries.add(entry);
//                            Log.d(TAG, document.getId() + " => " + document.getData());
//                        }
//                        sleepEntries.postValue(entries);
//                    } else {
//                        Log.d(TAG, "Error getting documents: ", task.getException());
//                    }
//
//                }
//            });
//        return sleepEntries;
//    }

//    public void addSleepEntry(String bid, SleepEntry entry){
//        CollectionReference reportsRef = babiesCollection.document(bid).collection("sleep_reports");
//        reportsRef.add(entry)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Log.d(TAG, "onSuccess: Added sleep entry to db.");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: Failed to add sleep entry to db.");
//            }
//        });
//    }

    private static ReportEntry documentToEntry(QueryDocumentSnapshot document, Constants.ReportType type){
        ReportEntry entry;
        switch (type){
            case SLEEP:
                entry = document.toObject(SleepEntry.class);
                break;
            case FOOD:
                entry = document.toObject(FoodEntry.class);
                break;
            case DIAPER:
                entry = document.toObject(DiaperEntry.class);
                break;
            default:
                Log.w(TAG, "documentToEntry: illegal report type");
                return null;
        }
        return entry;
    }

    public LiveData<List<ReportEntry>> getSleepEntries(String bid){
        String collectionName = reportToCollection.get(Constants.ReportType.SLEEP);
        babiesCollection.document(bid)
                .collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<ReportEntry> entries = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SleepEntry entry = (SleepEntry) documentToEntry(document, Constants.ReportType.SLEEP);
                                entries.add(entry);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            sleepEntries.postValue(entries);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return sleepEntries;
    }

    public LiveData<List<ReportEntry>> getFoodEntries(String bid){
        String collectionName = reportToCollection.get(Constants.ReportType.FOOD);
        babiesCollection.document(bid)
                .collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<ReportEntry> entries = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FoodEntry entry = (FoodEntry) documentToEntry(document, Constants.ReportType.FOOD);
                                entries.add(entry);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            foodEntries.postValue(entries);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return foodEntries;
    }


    public LiveData<List<ReportEntry>> getDiaperEntries(String bid){
        String collectionName = reportToCollection.get(Constants.ReportType.DIAPER);
        babiesCollection.document(bid)
                .collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<ReportEntry> entries = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DiaperEntry entry = (DiaperEntry) documentToEntry(document, Constants.ReportType.DIAPER);
                                entries.add(entry);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            diaperEntries.postValue(entries);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return diaperEntries;
    }

//    public LiveData<List<ReportEntry>> getEntries(String bid, final Constants.ReportType type){
//        String collectionName = reportToCollection.get(type);
//        babiesCollection.document(bid)
//                .collection(collectionName)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        List<ReportEntry> entries = new ArrayList<>();
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                ReportEntry entry = documentToEntry(document, type);
//                                entries.add(entry);
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                            curEntries.postValue(entries);
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//        return curEntries;
//    }

    public void addEntry(Constants.ReportType type, String bid, ReportEntry entry){
        String collectionName = reportToCollection.get(type);
        CollectionReference reportsRef = babiesCollection.document(bid).collection(collectionName);
        reportsRef.add(entry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: Added entry to db.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to add entry to db.");
            }
        });
    }

}
