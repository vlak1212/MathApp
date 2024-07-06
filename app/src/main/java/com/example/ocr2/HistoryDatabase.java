package com.example.ocr2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ocr2.DAO.HistoryDao;

@Database(entities = {HistoryItem.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {
    private static HistoryDatabase instance;

    public abstract HistoryDao historyDao();

    public static synchronized HistoryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            HistoryDatabase.class, "history_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    public void deleteHistoryItem(HistoryItem item) {
        new Thread(() -> historyDao().delete(item)).start();
    }
}
