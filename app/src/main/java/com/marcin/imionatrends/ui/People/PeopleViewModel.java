package com.marcin.imionatrends.ui.People;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marcin.imionatrends.data.DatabaseHelper;
import com.marcin.imionatrends.data.LiveFirstNameData;

import java.util.List;

public class PeopleViewModel extends AndroidViewModel {
    private final MutableLiveData<List<LiveFirstNameData>> liveFirstNameData;

    public PeopleViewModel(@NonNull Application application) {
        super(application);
        liveFirstNameData = new MutableLiveData<>();
        loadLiveFirstNameData(application);
    }

    public LiveData<List<LiveFirstNameData>> getLiveFirstNameData() {
        return liveFirstNameData;
    }

    private void loadLiveFirstNameData(Application application) {
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        List<LiveFirstNameData> data = dbHelper.getAllLiveFirstNameData();
        liveFirstNameData.setValue(data);
    }
}
