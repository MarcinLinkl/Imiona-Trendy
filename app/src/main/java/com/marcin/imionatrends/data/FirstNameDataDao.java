package com.marcin.imionatrends.data;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FirstNameDataDao {

    @Query("DELETE FROM firstname_data")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FirstNameData firstNameData);

    @Query("SELECT * FROM firstname_data")
    List<FirstNameData> getAll();

    @Query("SELECT * FROM firstname_data WHERE year = :year")
    List<FirstNameData> getByYear(int year);


    @Query("SELECT COUNT(DISTINCT year) FROM firstname_data")
    int countDistinctYears();

}
