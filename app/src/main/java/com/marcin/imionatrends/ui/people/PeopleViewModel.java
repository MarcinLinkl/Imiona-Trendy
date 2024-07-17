package com.marcin.imionatrends.ui.people;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.marcin.imionatrends.data.DatabaseHelper;
import com.marcin.imionatrends.data.LiveFirstNameData;
import java.util.ArrayList;
import java.util.List;

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

    private void loadLiveFirstNameData() {
        List<LiveFirstNameData> data = dbHelper.getAllLiveFirstNameData();
        liveFirstNameData.setValue(data);
    }

    public void filterData(String query, String gender) {
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
        liveFirstNameData.setValue(filteredList);
    }
}
