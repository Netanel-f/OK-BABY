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

    public void addSleepEntry(String bid, SleepEntry entry) {
        dbRepo.addEntry(Constants.ReportType.SLEEP, bid, entry);
    }

    public void addFoodEntry(String bid, FoodEntry entry) {
        dbRepo.addEntry(Constants.ReportType.FOOD, bid, entry);
    }

    public void addDiaperEntry(String bid, DiaperEntry entry) {
        dbRepo.addEntry(Constants.ReportType.DIAPER, bid, entry);
    }

    public boolean isFirstTimeSleep() {
        return isFirstTimeSleep;
    }

    public void setIsFirstTimeSleep(boolean isFirstTimeSleep) {
        this.isFirstTimeSleep = isFirstTimeSleep;
    }

    public LiveData<List<ReportEntry>> getSleepEntries(String bid) {
        return dbRepo.getSleepEntries(bid);
    }

    public boolean isFirstTimeFood() {
        return isFirstTimeFood;
    }

    public void setIsFirstTimeFood(boolean isFirstTimeFood) {
        this.isFirstTimeFood = isFirstTimeFood;
    }

    public LiveData<List<ReportEntry>> getFoodEntries(String bid) {
        return dbRepo.getFoodEntries(bid);
    }

    public boolean isFirstTimeDiaper() {
        return isFirstTimeDiaper;
    }

    public void setIsFirstTimeDiaper(boolean isFirstTimeDiaper) {
        this.isFirstTimeDiaper = isFirstTimeDiaper;
    }

    public LiveData<List<ReportEntry>> getDiaperEntries(String bid) {
        return dbRepo.getDiaperEntries(bid);
    }
}
