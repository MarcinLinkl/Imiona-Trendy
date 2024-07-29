package com.marcin.imionatrends.ui.charts;

import android.app.Application;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.mikephil.charting.data.LineDataSet;
import com.marcin.imionatrends.data.DatabaseHelper;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChartsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<String>> uniqueNames;
    private final MutableLiveData<LineData> chartData;
    private final DatabaseHelper databaseHelper;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    private final List<String> namesInChart = new ArrayList<>();
    private boolean isPercentageValue = false;
    // Lista kolorów do przypisania do wykresów
    private static final int[] COLORS = {
            Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.YELLOW
    };
    public ChartsViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        uniqueNames = new MutableLiveData<>();
        chartData = new MutableLiveData<>();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        loadUniqueNames();
    }

    public LiveData<List<String>> getUniqueNames() {
        return uniqueNames;
    }

    public LiveData<LineData> getChartData() {
        return chartData;
    }

    private void loadUniqueNames() {
        executorService.execute(() -> {
            List<String> names = databaseHelper.getAllUniqueNames();
            mainHandler.post(() -> uniqueNames.setValue(names));
        });
    }

    public void addNameToChart(String name) {
        if (!namesInChart.contains(name)) {
            namesInChart.add(name);
            updateChartData();
        }
    }

    public void removeNameFromChart(String name) {
        namesInChart.remove(name);
        updateChartData();
    }

    public void setPercentageValue(boolean isPercentage) {
        this.isPercentageValue = isPercentage;
        updateChartData();
    }


    private void updateChartData() {
        executorService.execute(() -> {
            LineData combinedLineData = new LineData();
            int colorIndex = 0;
            for (String name : namesInChart) {
                LineData lineData = databaseHelper.getChartDataForName(name, isPercentageValue);
                if (lineData != null) {
                    // Add all datasets from the lineData to the combinedLineData
                    for (int i = 0; i < lineData.getDataSetCount(); i++) {
                        LineDataSet dataSet = (LineDataSet) lineData.getDataSetByIndex(i);
                        // Set the label to the name
                        dataSet.setLabel(name);
                        // Set a color for the dataset
                        if (colorIndex >= COLORS.length) {
                            colorIndex = 0; // Wrap around if there are more datasets than colors
                        }
                        dataSet.setColor(COLORS[colorIndex]);
                        dataSet.setValueTextColor(COLORS[colorIndex]);
                        combinedLineData.addDataSet(dataSet);
                        colorIndex++;
                    }
                }
            }
            mainHandler.post(() -> chartData.setValue(combinedLineData));
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
