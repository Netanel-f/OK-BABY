package com.ux.ok_baby.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ux.ok_baby.model.DiaperEntry;
import com.ux.ok_baby.model.FoodEntry;
import com.ux.ok_baby.model.ReportEntry;
import com.ux.ok_baby.model.SleepEntry;
import com.ux.ok_baby.repository.FirestoreRepository;
import com.ux.ok_baby.utils.Constants;

import java.util.List;

public class EntriesViewModel extends AndroidViewModel {

    private static final String TAG = "EntriesViewModel";
    private FirestoreRepository dbRepo;
    private boolean isFirstTimeDiaper = true, isFirstTimeFood = true, isFirstTimeSleep = true;


    public EntriesViewModel(@NonNull Application application) {
        super(application);
        this.dbRepo = new FirestoreRepository();
    }


    /**
     * add sleep entry to db
     * @param bid baby id
     * @param entry SleepEntry object
     */
    public void addSleepEntry(String bid, SleepEntry entry) {
        dbRepo.addEntry(Constants.ReportType.SLEEP, bid, entry);
    }


    /**
     * add food entry to db
     * @param bid baby id
     * @param entry FoodEntry object
     */
    public void addFoodEntry(String bid, FoodEntry entry) {
        dbRepo.addEntry(Constants.ReportType.FOOD, bid, entry);
    }


    /**
     * add diaper entry to db
     * @param bid baby id
     * @param entry DiaperEntry object
     */
    public void addDiaperEntry(String bid, DiaperEntry entry) {
        dbRepo.addEntry(Constants.ReportType.DIAPER, bid, entry);
    }


    /**
     * checking if this is the first sleep entry
     * @return true iff the first sleep entry
     */
    public boolean isFirstTimeSleep() {
        return isFirstTimeSleep;
    }


    /**
     * set flag of first sleep entry
     * @param isFirstTimeSleep boolean to save to.
     */
    public void setIsFirstTimeSleep(boolean isFirstTimeSleep) {
        this.isFirstTimeSleep = isFirstTimeSleep;
    }


    /**
     * Get sleep entries list from DB
     * @param bid baby id
     * @return LiveData list of Report Entry
     */
    public LiveData<List<ReportEntry>> getSleepEntries(String bid) {
        return dbRepo.getSleepEntries(bid);
    }


    /**
     * checking if this is the first food entry
     * @return true iff the first food entry
     */
    public boolean isFirstTimeFood() {
        return isFirstTimeFood;
    }


    /**
     * set flag of first food entry
     * @param isFirstTimeFood boolean to save to.
     */
    public void setIsFirstTimeFood(boolean isFirstTimeFood) {
        this.isFirstTimeFood = isFirstTimeFood;
    }


    /**
     * Get food entries list from DB
     * @param bid baby id
     * @return LiveData list of Report Entry
     */
    public LiveData<List<ReportEntry>> getFoodEntries(String bid) {
        return dbRepo.getFoodEntries(bid);
    }


    /**
     * checking if this is the first food entry
     * @return true iff the first food entry
     */
    public boolean isFirstTimeDiaper() {
        return isFirstTimeDiaper;
    }


    /**
     * set flag of first food entry
     * @param isFirstTimeDiaper boolean to save to.
     */
    public void setIsFirstTimeDiaper(boolean isFirstTimeDiaper) {
        this.isFirstTimeDiaper = isFirstTimeDiaper;
    }


    /**
     * Get diaper entries list from DB
     * @param bid baby id
     * @return LiveData list of Report Entry
     */
    public LiveData<List<ReportEntry>> getDiaperEntries(String bid) {
        return dbRepo.getDiaperEntries(bid);
    }
}
