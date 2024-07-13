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

import android.content.Context;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(context);

                // Check if first name data already exists
                if (dbHelper.isFirstNameDataEmpty()) {
                    downloadAndInsertCSVGivenNames(dbHelper, CSV_GIVEN_NAMES_URL_2000_2019);

                    for (Map.Entry<String, Integer> entry : CSV_GIVEN_2020_2023_MAP.entrySet()) {
                        downloadAndInsertCSVGivenNames(dbHelper, entry.getKey(), entry.getValue());
                    }
                }

                // Check if live first name data already exists
                if (dbHelper.isLiveFirstNameDataEmpty()) {
                    downloadAndInsertCSVLiveNames(dbHelper, CSV_NAMES_LIVE_MALE_FROM_2024, true);
                    downloadAndInsertCSVLiveNames(dbHelper, CSV_NAMES_LIVE_FEMALE_FROM_2024, false);
                }

                // Notify success
                onSuccess.run();
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                onFailure.run();
            }
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            Log.d(TAG, "Time elapsed: " + elapsedTime/1000 + "s");
        }).start();
    }

    private static void downloadAndInsertCSVGivenNames(DatabaseHelper dbHelper, String urlString) throws IOException, CsvException {
        dbHelper.downloadAndInsertCSVGivenNames(urlString);
    }

    private static void downloadAndInsertCSVGivenNames(DatabaseHelper dbHelper, String urlString, int year) throws IOException, CsvException {
        dbHelper.downloadAndInsertCSVGivenNames(urlString,year);
    }

    private static void downloadAndInsertCSVLiveNames(DatabaseHelper dbHelper, String urlString, boolean isMale) throws IOException, CsvException {
        dbHelper.downloadAndInsertCSVLiveNames(urlString, isMale);
    }
}
