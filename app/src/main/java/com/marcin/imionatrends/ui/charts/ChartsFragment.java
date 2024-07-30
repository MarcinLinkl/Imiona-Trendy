package com.marcin.imionatrends.ui.charts;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.chip.Chip;
import com.marcin.imionatrends.R;
import com.marcin.imionatrends.databinding.FragmentChartsBinding;
import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

public class ChartsFragment extends Fragment {

    private FragmentChartsBinding binding;
    private ChartsViewModel chartsViewModel;
    private LineChart lineChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChartsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up ViewModel
        chartsViewModel = new ViewModelProvider(this).get(ChartsViewModel.class);
        binding.setViewModel(chartsViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Initialize LineChart
        lineChart = binding.lineChart;

        // Setup observers and listeners
        setupObservers();
        setupListeners();
        setupSpinner();
        styleLineChart();
        return root;
    }

    private void setupObservers() {
        chartsViewModel.getUniqueNames().observe(getViewLifecycleOwner(), names -> {
            if (names != null) {
                setupSpinnerAdapter(names);
            }
        });

        chartsViewModel.getChartData().observe(getViewLifecycleOwner(), lineData -> {
            if (lineData != null) {
                lineChart.setData(lineData);

                // Customize the X-axis to display years without commas
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        // Format the x value to be a year (e.g., 2020 instead of 2020.0)
                        return String.format("%d", (int) value);
                    }
                });

                lineChart.invalidate(); // Refresh the chart
            }
        });
    }

    private void setupListeners() {
        binding.switchPercentage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chartsViewModel.setPercentageValue(isChecked);
        });
    }

    private void setupSpinner() {
        binding.nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) parent.getItemAtPosition(position);
                chartsViewModel.addNameToChart(selectedName);
                addChipToGroup(selectedName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void setupSpinnerAdapter(List<String> names) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.nameSpinner.setAdapter(spinnerAdapter);
    }

    private void addChipToGroup(String name) {
        if (binding.chipGroup.findViewWithTag(name) == null) {
            Chip chip = new Chip(getContext());
            chip.setText(name);
            chip.setTag(name);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                binding.chipGroup.removeView(chip);
                chartsViewModel.removeNameFromChart(name);
            });
            binding.chipGroup.addView(chip);
        }
    }

    private void styleLineChart() {
        // Customize line chart appearance
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);

        lineChart.getXAxis().setTextColor(ContextCompat.getColor(getContext(), R.color.line_chart_axis_color));
        lineChart.getAxisLeft().setTextColor(ContextCompat.getColor(getContext(), R.color.line_chart_axis_color));
        lineChart.getAxisRight().setTextColor(ContextCompat.getColor(getContext(), R.color.line_chart_axis_color));

        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setGridColor(ContextCompat.getColor(getContext(), R.color.line_chart_grid_color));
        lineChart.getAxisLeft().setGridLineWidth(1f);
        lineChart.getLegend().setTextColor(ContextCompat.getColor(getContext(), R.color.line_chart_legend_color));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh chart data when fragment resumes
        chartsViewModel.updateChartData();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        chartsViewModel.resetChartData(); // Reset chart data when fragment is destroyed
        binding = null; // Clean up binding to avoid memory leaks
    }
}
