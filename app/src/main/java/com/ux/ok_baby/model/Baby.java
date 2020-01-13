package com.ux.ok_baby.Model;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Baby implements Parcelable {

    private String bid;
    private String babyName;
    private String babyDOB;
    private String imageUrl;

    public Baby(){
//        this.babyName = "";
//        this.babyDOB = "";
    }

    public Baby(@NonNull String bid, String babyName, String babyDOB) {
        this.bid = bid;
        this.babyName = babyName;
        this.babyDOB = babyDOB;
    }

    protected Baby(Parcel in) {
        bid = in.readString();
        babyName = in.readString();
        babyDOB = in.readString();
        imageUrl = in.readString();
    }

    public String getBid() {
        return this.bid;
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

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /* Parcelable */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bid);
        parcel.writeString(babyName);
        parcel.writeString(babyDOB);
        parcel.writeString(imageUrl);
    }

    public static final Creator<Baby> CREATOR = new Creator<Baby>() {
        @Override
        public Baby createFromParcel(Parcel parcel) {
            return new Baby(parcel);
        }

        @Override
        public Baby[] newArray(int size) {
            return new Baby[size];
        }
    };

}
