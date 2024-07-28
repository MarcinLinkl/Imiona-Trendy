package com.marcin.imionatrends.ui.people;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marcin.imionatrends.data.DatabaseHelper;
import com.marcin.imionatrends.data.LiveFirstNameData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeopleViewModel extends AndroidViewModel {
    private final MutableLiveData<List<LiveFirstNameData>> liveFirstNameData;
    private final MutableLiveData<List<LiveFirstNameData>> originalLiveFirstNameData; // New LiveData for original data
    private final DatabaseHelper dbHelper;

    private String currentGender = "Wszyscy";

    public PeopleViewModel(@NonNull Application application) {
        super(application);
        liveFirstNameData = new MutableLiveData<>();
        originalLiveFirstNameData = new MutableLiveData<>(); // Initialize original data
        dbHelper = new DatabaseHelper(application);
        loadLiveFirstNameData("Wszyscy");
    }

    public LiveData<List<LiveFirstNameData>> getLiveFirstNameData() {
        return liveFirstNameData;
    }

    public LiveData<List<LiveFirstNameData>> getOriginalLiveFirstNameData() {
        return originalLiveFirstNameData;
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private void loadLiveFirstNameData(String gender) {
        executorService.execute(() -> {
            List<LiveFirstNameData> data = dbHelper.getAllLiveFirstNameDataByGender(gender);
            mainHandler.post(() -> {
                liveFirstNameData.setValue(data);
                originalLiveFirstNameData.setValue(new ArrayList<>(data)); // Save a copy of original data
            });
        });
    }

    public void updateGender(String newGender) {
        currentGender = newGender;
        loadLiveFirstNameData(currentGender);
    }

    // Method to filter data based on query
    public void filterData(String query) {
        executorService.execute(() -> {
            List<LiveFirstNameData> originalData = originalLiveFirstNameData.getValue();
            if (originalData != null) {
                List<LiveFirstNameData> filteredList = new ArrayList<>();
                String queryLowerCase = query.toLowerCase();

                for (LiveFirstNameData item : originalData) {
                    boolean matchesName = item.getName().toLowerCase().contains(queryLowerCase);
                    boolean matchesGender = "Wszyscy".equals(currentGender) ||
                            ("Mężczyźni".equals(currentGender) && item.getIsMale() == 1) ||
                            ("Kobiety".equals(currentGender) && item.getIsMale() == 0);

                    if (matchesName && matchesGender) {
                        filteredList.add(item);
                    }
                }

                mainHandler.post(() -> liveFirstNameData.setValue(filteredList));
            }
        });
    }
}
