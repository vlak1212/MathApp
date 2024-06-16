package com.example.ocr2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_table")
public class HistoryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String equation;
    public String solution;

    public HistoryItem(String equation, String solution) {
        this.equation = equation;
        this.solution = solution;
    }
}
