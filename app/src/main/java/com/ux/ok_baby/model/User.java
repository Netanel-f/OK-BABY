package com.ux.ok_baby.model;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

/**
 * Represents a User object.
 */
public class User {
    private String uid;
    private String email;
    private List<DocumentReference> babies;

    /**
     * Empty constructor.
     */
    public User(){}

    /**
     * Details constructor.
     * @param uid - user id.
     * @param email - email of the user.
     * @param babies - list of references to babies.
     */
    public User(String uid, String email, List<DocumentReference> babies) {
        this.uid = uid;
        this.email = email;
        this.babies = babies;
    }

    /**
     * @return user id.
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid - user id.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email - email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return list of babies references.
     */
    public List<DocumentReference> getBabies() {
        return babies;
    }

    /**
     * @param babies - list of babies references.
     */
    public void setBabies(List<DocumentReference> babies) {
        this.babies = babies;
    }
}
