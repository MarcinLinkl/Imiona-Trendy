package com.marcin.imionatrends.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LiveFirstNameDataDao {
    @Insert
    void insert(LiveFirstNameData data);
    @Query("SELECT COUNT(*) FROM live_firstname_data")
    int count();
    @Query("DELETE FROM live_firstname_data")
    void deleteAll();
}