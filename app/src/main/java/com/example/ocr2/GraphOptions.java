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

        Button btnLinear = findViewById(R.id.btnLinear);
        Button btnQuadratic = findViewById(R.id.btnQuadratic);
        Button btnCubic = findViewById(R.id.btnCubic);
        Button btnQuartic = findViewById(R.id.btnQuartic);
        Button btnSin = findViewById(R.id.btnSin);
        Button btnCos = findViewById(R.id.btnCos);
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
            } else
                return false;
        });
        btnLinear.setOnClickListener(v -> startPlotGraphActivity(1));
        btnQuadratic.setOnClickListener(v -> startPlotGraphActivity(2));
        btnCubic.setOnClickListener(v -> startPlotGraphActivity(3));
        btnQuartic.setOnClickListener(v -> startPlotGraphActivity(4));
        btnSin.setOnClickListener(v -> startPlotGraphActivity(5));
        btnCos.setOnClickListener(v -> startPlotGraphActivity(6));
    }

    private void startPlotGraphActivity(int equationType) {
        Intent intent = new Intent(this, PlotGraph.class);
        intent.putExtra("equationType", equationType);
        startActivity(intent);
    }
}