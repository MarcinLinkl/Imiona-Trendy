package com.marcin.imionatrends.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {FirstNameData.class, LiveFirstNameData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FirstNameDataDao firstNameDataDao();
    public abstract LiveFirstNameDataDao liveFirstNameDataDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}