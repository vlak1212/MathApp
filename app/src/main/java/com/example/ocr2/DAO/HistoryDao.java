package com.example.ocr2.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ocr2.HistoryItem;

import java.util.List;

@Dao
public interface HistoryDao {
    @Insert
    void insert(HistoryItem item);
    @Query("SELECT * FROM history_table ORDER BY id DESC")
    List<HistoryItem> getAllHistory();
    @Query("DELETE FROM history_table WHERE id = :id")
    void deleteById(int id);
    @Delete
    void delete(HistoryItem item);
}
