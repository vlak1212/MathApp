package com.example.ocr2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.ocr2.Adapter.PostAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class Forum extends AppCompatActivity {

    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnCreatePost = findViewById(R.id.buttonCreatePost);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_forum);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_forum) {
                return true;
            } else if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(Forum.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_calculator) {
                startActivity(new Intent(Forum.this, Calculator.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_graph) {
                startActivity(new Intent(Forum.this, GraphOptions.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_history) {
                startActivity(new Intent(Forum.this, History.class));
                overridePendingTransition(0, 0);
                return true;
            } else
                return false;
        });
        btnCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(Forum.this, CreatePost.class);
            startActivity(intent);
        });

        loadPosts();
    }

    private void loadPosts() {
        FirebaseHelper.getAllPosts(new FirebaseHelper.PostCallback() {
            @Override
            public void onCallback(List<Post> postList) {
                postAdapter = new PostAdapter(Forum.this, postList);
                recyclerViewPosts.setAdapter(postAdapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts();
    }
}
