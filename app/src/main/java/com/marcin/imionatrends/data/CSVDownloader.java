package com.marcin.imionatrends.data;

import static android.content.ContentValues.TAG;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CSVDownloader {
    private static final String CSV_GIVEN_NAMES_URL_2000_2019 = "https://api.dane.gov.pl/resources/21458,imiona-nadane-dzieciom-w-polsce-w-latach-2000-2019-imie-pierwsze/csv";
    private static final Map<String, Integer> CSV_GIVEN_2020_2023_MAP = Map.of(
            "https://api.dane.gov.pl/resources/28020,imiona-meskie-nadane-dzieciom-w-polsce-w-2020-r-imie-pierwsze/csv", 2020,
            "https://api.dane.gov.pl/resources/28021,imiona-zenskie-nadane-dzieciom-w-polsce-w-2020-r-imie-pierwsze/csv", 2020,
            "https://api.dane.gov.pl/resources/36393,imiona-meskie-nadane-dzieciom-w-polsce-w-2021-r-imie-pierwsze/csv", 2021,
            "https://api.dane.gov.pl/resources/36394,imiona-zenskie-nadane-dzieciom-w-polsce-w-2021-r-imie-pierwsze/csv", 2021,
            "https://api.dane.gov.pl/resources/44825,imiona-meskie-nadane-dzieciom-w-polsce-w-2022-r-imie-pierwsze/csv", 2022,
            "https://api.dane.gov.pl/resources/44824,imiona-zenskie-nadane-dzieciom-w-polsce-w-2022-r-imie-pierwsze/csv", 2022,
            "https://api.dane.gov.pl/resources/54099,imiona-meskie-nadane-dzieciom-w-polsce-w-2023-r-imie-pierwsze/csv", 2023,
            "https://api.dane.gov.pl/resources/54100,imiona-zenskie-nadane-dzieciom-w-polsce-w-2023-r-imie-pierwsze/csv", 2023
    );
    private static final String CSV_NAMES_LIVE_MALE_FROM_2024 = "https://api.dane.gov.pl/resources/54109,lista-imion-meskich-w-rejestrze-pesel-stan-na-19012023-imie-pierwsze/csv";
    private static final String CSV_NAMES_LIVE_FEMALE_FROM_2024 = "https://api.dane.gov.pl/resources/54110,lista-imion-zenskich-w-rejestrze-pesel-stan-na-19012024-imie-pierwsze/csv";

    public static void downloadCsvData(Context context, Runnable onSuccess, Runnable onFailure) {
        ExecutorService executor = Executors.newFixedThreadPool(4); // Ustawiamy 2 wątki dla lepszej wydajności
        executor.submit(() ->  {
            AppDatabase db = AppDatabase.getDatabase(context);

            try {
                Log.d(TAG, "years: " +db.firstNameDataDao().countDistinctYears());
                // Check if first name data already exists
                if (db.firstNameDataDao().countDistinctYears() <= 23) {
                    showToast(context, "Start downloading of first name data");
                    db.firstNameDataDao().deleteAll();

                    // Download and process data from 2000-2019
                    downloadAndInsertCSVGivenNames(db, CSV_GIVEN_NAMES_URL_2000_2019);

                    // Download and process data from 2020-2023
                    for (Map.Entry<String, Integer> entry : CSV_GIVEN_2020_2023_MAP.entrySet()) {
                        downloadAndInsertCSVGivenNames(db, entry.getKey(), entry.getValue());
                    }

                    // Show information about database update
                    showToast(context, "Database update completed successfully");
                } else {
                    // Show information that data is already in the database
                    showToast(context, "Data FirstName Given is already available in the database");
                }

                // Download and process live data if the table is empty
                // 61k is for now number for names live in Pesel
                Log.d(TAG, "live names: " +db.liveFirstNameDataDao().count());
                if (db.liveFirstNameDataDao().count() <= 61000) {
                    showToast(context, "SStart downloading of live first name data");
                    db.liveFirstNameDataDao().deleteAll();

                    downloadAndInsertCSVLiveNames(db, CSV_NAMES_LIVE_MALE_FROM_2024, true);
                    downloadAndInsertCSVLiveNames(db, CSV_NAMES_LIVE_FEMALE_FROM_2024, false);
                }
                else {
                    showToast(context, "Data LiveFirstName from PESEL is already available in the database");
                }

                onSuccess.run();
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                onFailure.run();
                showToast(context, "Error updating the database");
            } finally {
                executor.shutdown(); // Zamykamy executor po użyciu
            }
        });
    }

    private static void showToast(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }

    private static void downloadAndInsertCSVGivenNames(AppDatabase db, String urlString, int year) throws IOException, CsvException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try (CSVReader reader = new CSVReader(new InputStreamReader(urlConnection.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] columns = rows.get(i);
                String name = columns[0];
                int count = Integer.parseInt(columns[2]);
                boolean isMale = columns[1].toUpperCase().startsWith("M");
                FirstNameData firstNameData = new FirstNameData(year, name, count, isMale);
                db.firstNameDataDao().insert(firstNameData);
            }
            Log.d(TAG, "downloadAndInsert CSV GivenNames: " + year);
        }
    }

    private static void downloadAndInsertCSVGivenNames(AppDatabase db, String urlString) throws IOException, CsvException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try (CSVReader reader = new CSVReader(new InputStreamReader(urlConnection.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] columns = rows.get(i);
                int year = Integer.parseInt(columns[0]);
                String name = columns[1];
                int count = Integer.parseInt(columns[2]);
                boolean isMale = columns[3].toUpperCase().startsWith("M");
                FirstNameData firstNameData = new FirstNameData(year, name, count, isMale);
                db.firstNameDataDao().insert(firstNameData);
            }
            Log.d(TAG, "downloadAndInsert CSV GivenNames: " + urlString);
        }
    }

    private static void downloadAndInsertCSVLiveNames(AppDatabase db, String urlString, boolean isMale) throws IOException, CsvException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try (CSVReader reader = new CSVReader(new InputStreamReader(urlConnection.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] columns = rows.get(i);
                String name = columns[0];
                int count = Integer.parseInt(columns[2]);
                LiveFirstNameData liveFirstNameData = new LiveFirstNameData(name, count, isMale);
                db.liveFirstNameDataDao().insert(liveFirstNameData);
            }
            Log.d(TAG, "downloadAndInsert CSV LiveGivenNames: " + (isMale ? "Male" : "Female"));
        }
    }
}
