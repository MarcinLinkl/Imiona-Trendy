package com.marcin.imionatrends.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "firstname_data")
public class FirstNameData {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public int year;
    public String name;
    public int count;
    public boolean isMale;

    public FirstNameData(int year, String name, int count, boolean isMale) {
        this.year = year;
        this.name = name;
        this.count = count;
        this.isMale = isMale;
    }
}