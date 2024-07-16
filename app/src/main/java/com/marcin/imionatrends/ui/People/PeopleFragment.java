package com.marcin.imionatrends.ui.People;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.marcin.imionatrends.R;
import java.util.ArrayList;

public class PeopleFragment extends Fragment {

    private PeopleViewModel peopleViewModel;
    private RecyclerView recyclerView;
    private LiveFirstNameDataAdapter adapter;
    private TextView emptyView;
    private EditText searchEditText;
    private Spinner genderSpinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_people, container, false);

        recyclerView = root.findViewById(R.id.recycler_view);
        emptyView = root.findViewById(R.id.empty_view);
        searchEditText = root.findViewById(R.id.search_edit_text);
        genderSpinner = root.findViewById(R.id.gender_spinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LiveFirstNameDataAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        peopleViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(PeopleViewModel.class);

        peopleViewModel.getLiveFirstNameData().observe(getViewLifecycleOwner(), liveFirstNameData -> {
            if (liveFirstNameData != null && !liveFirstNameData.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setLiveFirstNameData(liveFirstNameData);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return root;
    }

    private void filterData() {
        String query = searchEditText.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        peopleViewModel.filterData(query, gender);
    }
}
