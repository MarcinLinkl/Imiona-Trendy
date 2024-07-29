package com.marcin.imionatrends.ui.top;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marcin.imionatrends.data.DatabaseHelper;
import com.marcin.imionatrends.data.GivenFirstNameData;

import java.util.ArrayList;
import java.util.List;

public class TopViewModel extends AndroidViewModel {


    private final MutableLiveData<List<GivenFirstNameData>> firstNameData;

    private final MutableLiveData<List<Integer>> years = new MutableLiveData<>();
    private final DatabaseHelper databaseHelper;
    private String selectedYear = "2023"; // Domyślny rok
    private String selectedGender = "Wszyscy"; // Domyślna płeć

    public TopViewModel(Application application) {
        super(application);
        firstNameData = new MutableLiveData<>();
        databaseHelper = new DatabaseHelper(application);

        loadYears();
    }

    private void loadYears() {
        List<Integer> yearList = databaseHelper.getYears();
        if (yearList != null && !yearList.isEmpty()) {
            years.setValue(yearList); // Ensure years is not null
            selectedYear = yearList.get(0).toString(); // Ustaw domyślny rok na pierwszy rok z listy
            loadData(); // Załaduj dane dla domyślnego roku
        } else {
            years.setValue(new ArrayList<>()); // Ensure years is not null
        }
    }

    public LiveData<List<Integer>> getYears() {
        return years;
    }

    public LiveData<List<GivenFirstNameData>> getFirstNameData() {
        return firstNameData;
    }

    public void filterData(String query, String gender) {
        List<GivenFirstNameData> data = databaseHelper.getRankingByYearAndGender(selectedYear, gender);
        if (!query.isEmpty()) {
            data = filterByName(data, query);
        }
        firstNameData.setValue(data);
    }

    public void filterData(String query) {
        List<GivenFirstNameData> data = firstNameData.getValue();
        if (data != null && !query.isEmpty()) {
            data = filterByName(data, query);
            firstNameData.setValue(data);
        }
    }

    private List<GivenFirstNameData> filterByName(List<GivenFirstNameData> data, String query) {
        List<GivenFirstNameData> filteredList = new ArrayList<>();
        for (GivenFirstNameData firstName : data) {
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

    public void updateYear(String newYear) {
        if (newYear != null && !newYear.isEmpty()) {
            selectedYear = newYear;
        }
        loadData();
    }

    private void loadData() {
        filterData("",selectedGender); // Możesz dostosować parametry, jeśli chcesz zaktualizować filtr
    }
}

