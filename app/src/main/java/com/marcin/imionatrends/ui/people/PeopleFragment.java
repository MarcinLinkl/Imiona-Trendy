package com.marcin.imionatrends.ui.people;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.marcin.imionatrends.databinding.FragmentPeopleBinding;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {

    private static final String TAG = "PeopleFragment";
    private FragmentPeopleBinding binding;
    private PeopleViewModel peopleViewModel;
    private LiveFirstNameDataAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout with Data Binding
        binding = FragmentPeopleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModel
        peopleViewModel = new ViewModelProvider(this).get(PeopleViewModel.class);

        // Bind ViewModel to layout
        binding.setViewModel(peopleViewModel);
        binding.setLifecycleOwner(this);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup observers and listeners
        setupObservers();
        setupListeners();

        return root;
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LiveFirstNameDataAdapter(new ArrayList<>(), new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);

        peopleViewModel.getLiveFirstNameData().observe(getViewLifecycleOwner(), liveFirstNameData -> {
            if (liveFirstNameData != null) {
                adapter.updateData(liveFirstNameData);

                // Handle visibility
                if (liveFirstNameData.isEmpty()) {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.emptyView.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.emptyView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupObservers() {
        peopleViewModel.getOriginalLiveFirstNameData().observe(getViewLifecycleOwner(), originalData -> {
            if (originalData != null) {
                if (adapter != null) {
                    adapter.updateOriginalData(originalData);
                } else {
                    Log.e(TAG, "Adapter is null when updating original data");
                }
            }
        });

        // Setup the gender spinner
        binding.genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);
                peopleViewModel.updateGender(selectedGender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupListeners() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                peopleViewModel.filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
