package com.marcin.imionatrends;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.marcin.imionatrends.data.CSVDownloader;
import com.marcin.imionatrends.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityMainBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_people, R.id.navigation_top, R.id.navigation_chart)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        updateDatabase();
    }

    private void updateDatabase() {
        CSVDownloader.downloadCsvData(this,
                () -> runOnUiThread(() -> Toast.makeText(MainActivity.this, "Checking And Updating Complete", Toast.LENGTH_SHORT).show()),
                () -> runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to download and complete data", Toast.LENGTH_SHORT).show()),
                () -> runOnUiThread(() -> Toast.makeText(MainActivity.this, "Data is missing or incomplete, starting download", Toast.LENGTH_SHORT).show()),
                () -> runOnUiThread(() -> Toast.makeText(MainActivity.this, "Data is already available", Toast.LENGTH_SHORT).show()));

    }

}

