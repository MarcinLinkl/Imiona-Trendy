package com.marcin.imionatrends.ui.charts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;
import java.util.List;

public class ChartsViewModel extends ViewModel {

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<List<String>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<LineData> chartData = new MutableLiveData<>();

    public ChartsViewModel() {
        searchQuery.setValue("");
        searchResults.setValue(new ArrayList<>());
        chartData.setValue(new LineData());
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        // Logic to update searchResults based on query
        // e.g., searchResults.setValue(fetchResults(query));
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
}
