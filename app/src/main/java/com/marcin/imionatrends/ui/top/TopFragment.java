package com.marcin.imionatrends.ui.top;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.marcin.imionatrends.R;
import com.marcin.imionatrends.databinding.FragmentTopBinding;

import java.util.List;

public class TopFragment extends Fragment {

    private FragmentTopBinding binding;
    private TopViewModel topViewModel;
    private GivenFirstNameDataAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTopBinding.inflate(inflater, container, false);
        View root = binding.getRoot(); // Use inflated binding root

        // Set up ViewModel
        topViewModel = new ViewModelProvider(this).get(TopViewModel.class);
        binding.setViewModel(topViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner()); // Use view lifecycle owner for better lifecycle handling

        // Setup RecyclerView
        setupRecyclerView();

        // Setup observers and listeners
        setupObservers();
        setupListeners();
        setupYearSpinner();

        return root;
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GivenFirstNameDataAdapter();
        binding.recyclerView.setAdapter(adapter);
        topViewModel.getFirstNameData().observe(getViewLifecycleOwner(), firstNameData -> {
            if (firstNameData != null) {
                adapter.updateData(firstNameData);
                // Handle visibility
                if (firstNameData.isEmpty()) {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.emptyView.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.emptyView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupYearSpinner() {
        topViewModel.getYears().observe(getViewLifecycleOwner(), years -> {
            if (years != null) {
                ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
                yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.yearSpinner.setAdapter(yearAdapter);

                // Ustaw domyślny rok na pierwszą wartość w spinnerze
                if (!years.isEmpty()) {
                    binding.yearSpinner.setSelection(0);
                    topViewModel.updateYear(years.get(0).toString());
                }
            }
        });
    }

    private void setupObservers() {
        topViewModel.getFirstNameData().observe(getViewLifecycleOwner(), firstNameData -> {
            if (adapter != null) {
                adapter.setData(firstNameData);
            }
        });
    }

    private void setupListeners() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                topViewModel.filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        binding.genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);
                topViewModel.updateGender(selectedGender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        binding.yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer selectedYear = (Integer) parent.getItemAtPosition(position);
                topViewModel.updateYear(selectedYear.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up binding to avoid memory leaks
    }
}
