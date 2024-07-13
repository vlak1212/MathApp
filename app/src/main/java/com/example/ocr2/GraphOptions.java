package com.example.ocr2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class GraphOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_options);

        Button btn1 = findViewById(R.id.buttonB1);
        Button btn2 = findViewById(R.id.buttonB2);
        Button btn3 = findViewById(R.id.buttonB3);
        Button btn4 = findViewById(R.id.buttonB4);
        Button btnLog = findViewById(R.id.buttonLogg);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_graph);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_graph) {
                return true;
            } else if (item.getItemId() == R.id.navigation_history) {
                startActivity(new Intent(GraphOptions.this, History.class));
                overridePendingTransition(0, 0);
                return true;
            }  else if (item.getItemId() == R.id.navigation_calculator) {
                startActivity(new Intent(GraphOptions.this, Calculator.class));
                overridePendingTransition(0, 0);
                return true;
            }  else if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(GraphOptions.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_forum) {
                startActivity(new Intent(GraphOptions.this, Forum.class));
                overridePendingTransition(0, 0);
                return true;
            } else
                return false;
        });
        btn1.setOnClickListener(v -> plotingGraph(1));
        btn2.setOnClickListener(v -> plotingGraph(2));
        btn3.setOnClickListener(v -> plotingGraph(3));
        btn4.setOnClickListener(v -> plotingGraph(4));
//        btnSin.setOnClickListener(v -> plotingGraph(5));
//        btnCos.setOnClickListener(v -> plotingGraph(6));
        btnLog.setOnClickListener(v -> plotingGraph(7));
    }

    private void plotingGraph(int equationType) {
        Intent intent = new Intent(this, PlotGraph.class);
        intent.putExtra("equationType", equationType);
        startActivity(intent);
    }
}