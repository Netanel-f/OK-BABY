package com.ux.ok_baby.Model;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Baby {

    private String bid;
    private String babyName;
    private String babyDOB;
//    private List<DocumentReference> babies;

    public Baby(){
        this.babyName = "";
        this.babyDOB = "";
    }

    public Baby(String bid, String babyName, String babyDOB) {
        this.bid = bid;
        this.babyName = babyName;
        this.babyDOB = babyDOB;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getBabyName() {
        return this.babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public void setBabyDOB(String dob) {
        this.babyDOB = dob;
    }

    public String getBabyDOB() {
        return this.babyDOB;
    }


//    public List<DocumentReference> getBabies() {
//        return babies;
//    }

//    public void setBabies(List<DocumentReference> babies) {
//        this.babies = babies;
//    }
}
