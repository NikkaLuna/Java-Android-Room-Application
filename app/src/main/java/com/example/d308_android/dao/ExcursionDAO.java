package com.example.d308_android.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308_android.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT MAX(excursionID) FROM excursions")
    int getLastExcursionID();

    @Query("SELECT * FROM EXCURSIONS ORDER BY excursionID ASC")
    List<Excursion> getAllExcursions();

    @Query("SELECT * FROM EXCURSIONS WHERE vacationID=:vacation ORDER BY excursionID ASC ")
    List<Excursion> getAssociatedExcursions(int vacation);

    @Query("SELECT * FROM excursions WHERE excursionID = :excursionID")
    Excursion getExcursionById(int excursionID);

}
