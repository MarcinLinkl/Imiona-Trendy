package com.marcin.imionatrends.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "live_firstname_data")
public class LiveFirstNameData {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int count;
    public boolean isMale;

    public LiveFirstNameData(String name, int count, boolean isMale) {
        this.name = name;
        this.count = count;
        this.isMale = isMale;
    }
}
