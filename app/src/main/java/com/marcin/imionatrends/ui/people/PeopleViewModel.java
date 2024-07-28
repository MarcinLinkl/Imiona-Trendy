package com.marcin.imionatrends.ui.people;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marcin.imionatrends.data.DatabaseHelper;
import com.marcin.imionatrends.data.GivenFirstNameData;
import com.marcin.imionatrends.data.LiveFirstNameData;

import java.util.ArrayList;
import java.util.List;

public class PeopleViewModel extends AndroidViewModel {

    private final MutableLiveData<List<LiveFirstNameData>> liveFirstNameData;
    private final MutableLiveData<List<LiveFirstNameData>> originalLiveFirstNameData;
    private final DatabaseHelper databaseHelper;
    private String selectedGender = "Wszyscy"; // Domyślna płeć

    public PeopleViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        liveFirstNameData = new MutableLiveData<>();
        originalLiveFirstNameData = new MutableLiveData<>();
        loadData(); // Załaduj dane domyślne
    }

    public LiveData<List<LiveFirstNameData>> getLiveFirstNameData() {
        return liveFirstNameData;
    }

    public LiveData<List<LiveFirstNameData>> getOriginalLiveFirstNameData() {
        return originalLiveFirstNameData;
    }

    public void filterData(String query) {
        List<LiveFirstNameData> data = databaseHelper.getRankingByYearAndGender("2023", selectedGender); // Używamy domyślnego roku, jeśli nie obsługujemy lat w tym widoku
        if (data != null) {
            if (!query.isEmpty()) {
                data = filterByName(data, query);
            }
            liveFirstNameData.setValue(data);
            originalLiveFirstNameData.setValue(data);
        }
    }

    private List<LiveFirstNameData> filterByName(List<LiveFirstNameData> data, String query) {
        List<LiveFirstNameData> filteredList = new ArrayList<>();
        for (LiveFirstNameData firstName : data) {
            if (firstName.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(firstName);
            }
        }
        return filteredList;
    }

    public void updateGender(String selectedGender) {
        this.selectedGender = selectedGender; // Zaktualizuj wybraną płeć
        loadData(); // Załaduj dane z nową płcią
    }

    private void loadData() {
        filterData(""); // Możesz dostosować parametry, jeśli chcesz zaktualizować filtr
    }
}
