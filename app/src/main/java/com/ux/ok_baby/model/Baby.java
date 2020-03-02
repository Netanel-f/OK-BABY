package com.ux.ok_baby.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Represents a Baby object.
 */
public class Baby implements Parcelable {

    private String bid;
    private String babyName;
    private String babyDOB;
    private String imageUrl;

    /**
     * Empty constructor.
     */
    public Baby() {
    }

    /**
     * Baby constructor by baby id.
     * @param bid - baby id.
     */
    public Baby(String bid) {
        this.bid = bid;
    }

    /**
     * Baby full details constructor.
     * @param bid - baby id.
     * @param babyName - name of the baby.
     * @param babyDOB - date of birth.
     */
    public Baby(@NonNull String bid, String babyName, String babyDOB) {
        this.bid = bid;
        this.babyName = babyName;
        this.babyDOB = babyDOB;
    }

    /**
     * Parcelable Baby.
     * @param in - parcel.
     */
    protected Baby(Parcel in) {
        bid = in.readString();
        babyName = in.readString();
        babyDOB = in.readString();
        imageUrl = in.readString();
    }

    /**
     * @return baby id.
     */
    public String getBid() {
        return this.bid;
    }

    /**
     * @param bid - baby id.
     */
    public void setBid(String bid) {
        this.bid = bid;
    }

    /**
     * @return baby name.
     */
    public String getBabyName() {
        return this.babyName;
    }

    /**
     * @param babyName .
     */
    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    /**
     * @param dob - date of birth.
     */
    public void setBabyDOB(String dob) {
        this.babyDOB = dob;
    }

    /**
     * @return baby date of birth.
     */
    public String getBabyDOB() {
        return this.babyDOB;
    }

    /**
     * @return URL of the profile image.
     */
    public String getImageUrl() {
        return this.imageUrl;
    }

    /**
     * @param imageUrl - URL of the profile image.
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Baby baby = (Baby) o;

        return baby.bid.equals(this.bid);
    }
}
