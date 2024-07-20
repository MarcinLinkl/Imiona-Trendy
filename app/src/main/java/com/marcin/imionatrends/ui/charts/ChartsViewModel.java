package com.marcin.imionatrends.ui.charts;

import android.app.Application;

import com.marcin.imionatrends.data.DatabaseHelper;
import com.github.mikephil.charting.data.LineData;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChartsViewModel extends AndroidViewModel {

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<List<String>> searchResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<LineData> chartData = new MutableLiveData<>(new LineData());
    private final MutableLiveData<Boolean> isPercentage = new MutableLiveData<>(false);

    private final DatabaseHelper databaseHelper;
    private List<String> uniqueNames = new ArrayList<>(); // Initialize uniqueNames

    public ChartsViewModel(Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        loadUniqueNames();
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        filterNames(query);
    }

    public LiveData<List<String>> getSearchResults() {
        return searchResults;
    }

    public LiveData<LineData> getChartData() {
        return chartData;
    }

    public void setChartData(LineData data) {
        chartData.setValue(data);
    }

    public LiveData<Boolean> getIsPercentage() {
        return isPercentage;
    }

    public void setIsPercentage(boolean percentage) {
        isPercentage.setValue(percentage);
        String query = searchQuery.getValue() != null ? searchQuery.getValue() : "";
        filterNames(query); // Re-filter based on the updated percentage mode
    }

    private void loadUniqueNames() {
        uniqueNames = databaseHelper.getAllUniqueNames();
        searchResults.setValue(uniqueNames);
    }

    private void filterNames(String query) {
        if (uniqueNames != null) {
            List<String> filteredNames = uniqueNames.stream()
                    .filter(name -> name.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            searchResults.setValue(filteredNames);
        }
    }

    public void updateChartData(String name) {
        boolean isPercentageValue = isPercentage.getValue() != null && isPercentage.getValue();
        LineData data = databaseHelper.getChartDataForName(name, isPercentageValue);
        setChartData(data); // Update chart data based on the selected name and percentage mode
    }
}
