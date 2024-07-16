package com.marcin.imionatrends.ui.top;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.RecyclerView;

import com.marcin.imionatrends.R;
import com.marcin.imionatrends.data.DatabaseHelper;
import com.marcin.imionatrends.databinding.FragmentTopBinding;

import java.util.List;


public class TopFragment extends Fragment {

    private FragmentTopBinding binding;
    private TopViewModel topViewModel;
    private RecyclerView recyclerView;
    private NameAdapter nameAdapter;
    private Spinner genderSpinner, yearSpinner;
    private EditText searchEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTopBinding.inflate(inflater, container, false);
        View root = binding.getRoot(); // Use inflated binding root

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        nameAdapter = new NameAdapter();
        recyclerView.setAdapter(nameAdapter);

        genderSpinner = binding.genderSpinner;
        yearSpinner = binding.yearSpinner;
        searchEditText = binding.searchEditText;

        // Set up ViewModel
        topViewModel = new ViewModelProvider(this).get(TopViewModel.class);

        // Set up gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Set up year spinner
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, dbHelper.getYears());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Observe data from ViewModel
        topViewModel.getFirstNameData().observe(getViewLifecycleOwner(), firstNameData -> {
            // Update data in adapter
            nameAdapter.setData(firstNameData);
        });

        // Set up search functionality
        searchEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filterData();
            }
        });

        genderSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }
        });

        yearSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }
        });

        return root;
    }

    private void filterData() {
        String query = searchEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();
        String year = yearSpinner.getSelectedItem().toString();
        topViewModel.filterData(query, gender, year);
    }

    // TextWatcher implementation
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not needed, but required by the interface
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not needed, but required by the interface
        }

        @Override
        public abstract void afterTextChanged(Editable s);
    }

    // AdapterView.OnItemSelectedListener implementation
    private abstract static class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Not needed, but required by the interface
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

