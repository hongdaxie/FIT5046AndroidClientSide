package com.example.assignment3;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ReportDao {
    @Query("SELECT * FROM report")
    List<Report> getAll();

    @Query("SELECT * FROM report WHERE userId = :userid")
    List<Report> getAllByUserid(int userid);

    @Query("SELECT * FROM report WHERE id = :id")
    Report findById(int id);

    @Insert
    void insert(Report report);

    @Delete
    void delete(Report report);

    @Update(onConflict = REPLACE)
    public void updateReport(Report report);

    @Query("DELETE FROM report")
    void deleteAll();

    @Query("DELETE FROM report WHERE userId = :userid")
    void delete(int userid);
}
