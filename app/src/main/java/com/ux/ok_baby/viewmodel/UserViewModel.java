package com.ux.ok_baby.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ux.ok_baby.model.Baby;
import com.ux.ok_baby.model.User;
import com.ux.ok_baby.repository.FirestoreRepository;

public class UserViewModel extends AndroidViewModel {

    private static final String TAG = "UserViewModel";
    private FirestoreRepository dbRepo;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.dbRepo = new FirestoreRepository();
    }

    /* BABY */

    /**
     * Add baby to database and to update its caretaker user in the database.
     * @param caretakerUid - UID of the user the baby is added to.
     * @param baby         - baby to add.
     */
    public void addBaby(String caretakerUid, Baby baby){
        dbRepo.addBaby(caretakerUid, baby);
    }


    /**
     * Update the baby in the database by the baby's bid.
     * @param baby - baby to update.
     */
    public void updateBaby(Baby baby){
        dbRepo.updateBaby(baby);
    }

    /**
     * Get baby from database.
     *
     * @param bid - id of the baby to get.
     * @return observable LiveData baby object from db.
     */
    public LiveData<Baby> getBaby(String bid){
        return dbRepo.getBaby(bid);
    }

    /* USER */

    /**
     * Add new user to database.
     * @param user - user to add.
     */
    public void addNewUser(User user){
        // create user
        dbRepo.updateUser(user);

        // add baby with bid to user
        // todo: check if doesn't try to add baby before there's a user
        dbRepo.addBaby(user.getUid(), null);
    }

    /**
     * Update user in db by uid.
     * @param user - user to update.
     */
    public void updateUser(User user){
        dbRepo.updateUser(user);
    }

    /**
     * Get user from database.
     * @param uid - id of the user to update.
     * @return observable LiveData user object from db.
     */
    public LiveData<User> getUser(String uid){
        return dbRepo.getUser(uid);
    }

}
