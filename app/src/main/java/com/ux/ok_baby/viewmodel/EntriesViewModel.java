package com.ux.ok_baby.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ReportFragment;

import com.ux.ok_baby.Model.DiaperEntry;
import com.ux.ok_baby.Model.FoodEntry;
import com.ux.ok_baby.Model.ReportEntry;
import com.ux.ok_baby.Model.SleepEntry;
import com.ux.ok_baby.repository.FirestoreRepository;
import com.ux.ok_baby.utils.Constants;

import java.util.List;

public class EntriesViewModel extends AndroidViewModel {

    private static final String TAG = "EntriesViewModel";
    private FirestoreRepository dbRepo;


    public EntriesViewModel(@NonNull Application application) {
        super(application);
        this.dbRepo = new FirestoreRepository();
    }

    public void addSleepEntry(String bid, SleepEntry entry){
        dbRepo.addEntry(Constants.ReportType.SLEEP, bid, entry);
    }

    public void addFoodEntry(String bid, FoodEntry entry){
        dbRepo.addEntry(Constants.ReportType.FOOD, bid, entry);
    }

    public void addDiaperEntry(String bid, DiaperEntry entry){
        dbRepo.addEntry(Constants.ReportType.DIAPER, bid, entry);
    }

    public LiveData<List<ReportEntry>> getSleepEntries(String bid){
        return dbRepo.getEntries(bid, Constants.ReportType.SLEEP);
    }

    public LiveData<List<ReportEntry>> getFoodEntries(String bid){
        return dbRepo.getEntries(bid, Constants.ReportType.FOOD);
    }

    public LiveData<List<ReportEntry>> getDiaperEntries(String bid){
        return dbRepo.getEntries(bid, Constants.ReportType.DIAPER);
    }

    // todo: query specific entries: by date or others
}
