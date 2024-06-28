package com.example.ocr2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class History extends AppCompatActivity {

    private HistoryDatabase db;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.BG1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_history);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_history) {
                return true;
            } else if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(History.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }  else if (item.getItemId() == R.id.navigation_calculator) {
                startActivity(new Intent(History.this, Calculator.class));
                overridePendingTransition(0, 0);
                return true;
            }  else if (item.getItemId() == R.id.navigation_graph) {
                startActivity(new Intent(History.this, GraphOptions.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_forum) {
                startActivity(new Intent(History.this, Forum.class));
                overridePendingTransition(0, 0);
                return true;
            } else
                return false;
        });
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
