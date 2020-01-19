package com.ux.ok_baby.Model;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class User {
    private String uid;
    private String email;
    private List<DocumentReference> babies;

    public User(){}

    public User(String uid, String email, List<DocumentReference> babies) {
        this.uid = uid;
        this.email = email;
        this.babies = babies;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<DocumentReference> getBabies() {
        return babies;
    }

    public void setBabies(List<DocumentReference> babies) {
        this.babies = babies;
    }
}
