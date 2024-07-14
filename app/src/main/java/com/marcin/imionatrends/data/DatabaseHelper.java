package com.marcin.imionatrends.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "imiona_trends.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_FIRST_NAME_DATA = "firstname_data";
    private static final String TABLE_LIVE_FIRST_NAME_DATA = "live_firstname_data";

    // Common column names
    private static final String KEY_ID = "id";

    // firstname_data table - column names
    private static final String KEY_YEAR = "year";
    private static final String KEY_NAME = "name";
    private static final String KEY_COUNT = "count";
    private static final String KEY_IS_MALE = "is_male";

    // live_firstname_data table - column names
    private static final String KEY_LIVE_NAME = "live_name";
    private static final String KEY_LIVE_COUNT = "live_count";
    private static final String KEY_IS_MALE_LIVE = "is_male";

    // Batch size for bulk insertion
    private static final int BATCH_SIZE = 10000;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create firstname_data table
        String CREATE_TABLE_FIRST_NAME_DATA = "CREATE TABLE " + TABLE_FIRST_NAME_DATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_YEAR + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_COUNT + " INTEGER,"
                + KEY_IS_MALE + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE_FIRST_NAME_DATA);

        // Create live_firstname_data table
        String CREATE_TABLE_LIVE_FIRST_NAME_DATA = "CREATE TABLE " + TABLE_LIVE_FIRST_NAME_DATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LIVE_NAME + " TEXT,"
                + KEY_LIVE_COUNT + " INTEGER,"
                + KEY_IS_MALE_LIVE + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE_LIVE_FIRST_NAME_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIRST_NAME_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIVE_FIRST_NAME_DATA);

        // Create tables again
        onCreate(db);
    }

    // Insert firstname_data records in batch
    public void insertFirstNameData(List<FirstNameData> firstNameDataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            String sql = "INSERT INTO " + TABLE_FIRST_NAME_DATA + " (" +
                    KEY_YEAR + ", " + KEY_NAME + ", " + KEY_COUNT + ", " + KEY_IS_MALE + ") " +
                    "VALUES (?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);

            for (FirstNameData data : firstNameDataList) {
                statement.clearBindings();
                statement.bindLong(1, data.getYear());
                statement.bindString(2, data.getName());
                statement.bindLong(3, data.getCount());
                statement.bindLong(4, data.isMale() ? 1 : 0);
                statement.execute();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Insert live_firstname_data records in batch

    // Insert live_firstname_data records in batch
    public void insertLiveFirstNameData(List<LiveFirstNameData> liveFirstNameDataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            String sql = "INSERT INTO " + TABLE_LIVE_FIRST_NAME_DATA + " (" +
                    KEY_LIVE_NAME + ", " + KEY_LIVE_COUNT + ", " + KEY_IS_MALE_LIVE + ") " +
                    "VALUES (?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);

            for (LiveFirstNameData data : liveFirstNameDataList) {
                statement.clearBindings();
                statement.bindString(1, data.getName());
                statement.bindLong(2, data.getCount());
                statement.bindLong(3, data.isMale() ? 1 : 0);
                statement.execute();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Check if firstname_data table is empty
    public boolean isFirstNameDataEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FIRST_NAME_DATA, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }

    // Check if live_firstname_data table is empty
    public boolean isLiveFirstNameDataEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LIVE_FIRST_NAME_DATA, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }

    // Get distinct years in firstname_data table
    public List<Integer> getDistinctYears() {
        List<Integer> years = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + KEY_YEAR + " FROM " + TABLE_FIRST_NAME_DATA, null);
        if (cursor.moveToFirst()) {
            do {
                years.add(cursor.getInt(cursor.getColumnIndex(KEY_YEAR)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return years;
    }

    // Download and insert data for given names from CSV URL
    public void downloadAndInsertCSVGivenNames(String urlString, int year) throws IOException, CsvException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            CSVReader csvReader = new CSVReader(reader)) {
            List<FirstNameData> batchData = new ArrayList<>();
            String[] nextLine;
            csvReader.readNext(); // Skip header
            while ((nextLine = csvReader.readNext()) != null) {
                String name = nextLine[0];
                int count = Integer.parseInt(nextLine[2]);
                boolean isMale = nextLine[1].toUpperCase().startsWith("M");
                FirstNameData firstNameData = new FirstNameData(year, name, count, isMale);
                batchData.add(firstNameData);
                if (batchData.size() >= BATCH_SIZE) {
                    insertFirstNameData(batchData); // Insert batch of data
                    batchData.clear();
                }
            }
            // Insert remaining data
            if (!batchData.isEmpty()) {
                insertFirstNameData(batchData); // Insert remaining data
            }
            Log.d(TAG, "Downloaded and inserted CSV GivenNames: " + urlString);
        }
    }

    public void downloadAndInsertCSVGivenNames(String urlString) throws IOException, CsvException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            CSVReader csvReader = new CSVReader(reader)) {
            List<FirstNameData> batchData = new ArrayList<>();
            String[] nextLine;
            csvReader.readNext(); // Skip header
            while ((nextLine = csvReader.readNext()) != null) {
                int year = Integer.parseInt(nextLine[0]);

                String name = nextLine[1];
                int count = Integer.parseInt(nextLine[2]);
                boolean isMale = nextLine[3].toUpperCase().startsWith("M");
                FirstNameData firstNameData = new FirstNameData(year, name, count, isMale);
                batchData.add(firstNameData);
                if (batchData.size() >= BATCH_SIZE) {
                    insertFirstNameData(batchData); // Insert batch of data
                    batchData.clear();
                }
            }
            // Insert remaining data
            if (!batchData.isEmpty()) {
                insertFirstNameData(batchData); // Insert remaining data
                batchData.clear();
            }
            Log.d(TAG, "Downloaded and inserted CSV GivenNames: " + urlString);
        }
    }

    // Download and insert live data from CSV URL
    public void downloadAndInsertCSVLiveNames(String urlString, boolean isMale) throws IOException, CsvException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {
            List<LiveFirstNameData> batchData = new ArrayList<>();
            String[] nextLine;
            csvReader.readNext();
            while ((nextLine = csvReader.readNext()) != null) {
                String name = nextLine[0];
                int count = Integer.parseInt(nextLine[2]);
                LiveFirstNameData liveFirstNameData = new LiveFirstNameData(name, count, isMale);
                batchData.add(liveFirstNameData);
                if (batchData.size() >= BATCH_SIZE) {
                    insertLiveFirstNameData(batchData); // Insert batch of data
                    batchData.clear();
                }
            }
            // Insert remaining data
            if (!batchData.isEmpty()) {
                insertLiveFirstNameData(batchData); // Insert remaining data
            }
            Log.d(TAG, "Downloaded and inserted CSV LiveNames: " + (isMale ? "Male" : "Female"));
        }
    }
}
