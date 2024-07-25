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
    private final DatabaseHelper dbHelper;

    public PeopleViewModel(@NonNull Application application) {
        super(application);
        liveFirstNameData = new MutableLiveData<>();
        dbHelper = new DatabaseHelper(application);
        loadLiveFirstNameData();
    }

    public LiveData<List<LiveFirstNameData>> getLiveFirstNameData() {
        return liveFirstNameData;
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private void loadLiveFirstNameData() {
        executorService.execute(() -> {
            List<LiveFirstNameData> data = dbHelper.getAllLiveFirstNameData();
            mainHandler.post(() -> liveFirstNameData.setValue(data));
        });
    }

    public void filterData(String query, String gender) {
        executorService.execute(() -> {
            List<LiveFirstNameData> data = dbHelper.getAllLiveFirstNameData();
            List<LiveFirstNameData> filteredList = new ArrayList<>();
            for (LiveFirstNameData item : data) {
                boolean matchesName = item.getName().toLowerCase().contains(query.toLowerCase());
                boolean matchesGender = "Wszyscy".equals(gender) ||
                        ("Mężczyźni".equals(gender) && item.getIsMale() == 1) ||
                        ("Kobiety".equals(gender) && item.getIsMale() == 0);
                if (matchesName && matchesGender) {
                    filteredList.add(item);
                }
            }
            mainHandler.post(() -> liveFirstNameData.setValue(filteredList));
        });
    }
}
