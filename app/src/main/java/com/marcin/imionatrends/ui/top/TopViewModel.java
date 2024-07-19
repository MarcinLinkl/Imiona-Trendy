package com.marcin.imionatrends.ui.top;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marcin.imionatrends.data.DatabaseHelper;
import com.marcin.imionatrends.data.GivenFirstNameData;

import java.util.ArrayList;
import java.util.List;

public class TopViewModel extends AndroidViewModel {

    private MutableLiveData<List<GivenFirstNameData>> firstNameData;
    private DatabaseHelper databaseHelper;

    public TopViewModel(Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        firstNameData = new MutableLiveData<>();
    }

    public LiveData<List<GivenFirstNameData>> getFirstNameData() {
        return firstNameData;
    }

    public void filterData(String query, String gender, String year) {
        List<GivenFirstNameData> data = databaseHelper.getRankingByYearAndGender(year, gender);
        if (!query.isEmpty()) {
            data = filterByName(data, query);
        }
        firstNameData.setValue(data);
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
}
