package com.example.ocr2;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class History extends AppCompatActivity {

    private HistoryDatabase db;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);

        db = HistoryDatabase.getInstance(this);
        loadHistory();

        adapter.setDelListener(new HistoryAdapter.delListener() {
            @Override
            public void onDeleteClick(HistoryItem historyItem) {
                AlertDialog.Builder builder = new AlertDialog.Builder(History.this);
                builder.setTitle("Xóa bài toán");
                builder.setMessage("Bạn có chắc chắn muốn xóa bài toán này?");
                builder.setPositiveButton("Xóa", (dialog, which) -> {
                    deleteHistoryItem(historyItem);
                    dialog.dismiss();
                });
                builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });
    }

    private void deleteHistoryItem(HistoryItem historyItem) {
        new Thread(() -> {
            db.historyDao().deleteById(historyItem.id);
            runOnUiThread(() -> {
                int position = adapter.historyList.indexOf(historyItem);
                if (position != -1) {
                    adapter.deleteItem(position);
                }
            });
        }).start();
    }

    private void loadHistory() {
        new Thread(() -> {
            List<HistoryItem> historyList = db.historyDao().getAllHistory();
            runOnUiThread(() -> adapter.setHistoryList(historyList));
        }).start();
    }
}
