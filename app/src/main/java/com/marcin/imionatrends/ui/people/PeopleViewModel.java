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

    private final MutableLiveData<List<LiveFirstNameData>> liveFirstNameData = new MutableLiveData<>();
    private final MutableLiveData<List<LiveFirstNameData>> originalLiveFirstNameData = new MutableLiveData<>();
    private final DatabaseHelper databaseHelper;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String selectedGender = "Wszyscy";

    public PeopleViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        loadData(); // Load default data
    }

    public LiveData<List<LiveFirstNameData>> getLiveFirstNameData() {
        return liveFirstNameData;
    }

    public LiveData<List<LiveFirstNameData>> getOriginalLiveFirstNameData() {
        return originalLiveFirstNameData;
    }

    private void loadData() {
        executorService.execute(() -> {
            try {
                List<LiveFirstNameData> data = databaseHelper.getAllLiveFirstNameDataByGender(selectedGender);
                mainHandler.post(() -> {
                    liveFirstNameData.setValue(data);
                    originalLiveFirstNameData.setValue(data);
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions (e.g., show error message)
            }
        });
    }

    public void filterData(String query) {
        executorService.execute(() -> {
            try {
                List<LiveFirstNameData> originalData = originalLiveFirstNameData.getValue();
                if (originalData != null) {
                    List<LiveFirstNameData> filteredList = new ArrayList<>();
                    String queryLowerCase = query.toLowerCase();

                    for (LiveFirstNameData item : originalData) {
                        if (item.getName().toLowerCase().contains(queryLowerCase)) {
                            filteredList.add(item);
                        }
                    }

                    mainHandler.post(() -> liveFirstNameData.setValue(filteredList));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions (e.g., show error message)
            }
        });
    }

    public void updateGender(String newGender) {
        if (!newGender.equals(this.selectedGender)) {
            this.selectedGender = newGender;
            loadData(); // Load data with the new gender
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown(); // Clean up ExecutorService
    }
}
