package com.example.ocr2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;

public class CreatePost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextEmail, editTextTitle, editTextContent;
    private ImageView imageViewSelected;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        editTextEmail = findViewById(R.id.editEmail);
        editTextTitle = findViewById(R.id.editTitle);
        editTextContent = findViewById(R.id.editContent);
        imageViewSelected = findViewById(R.id.imageSelected);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnPost = findViewById(R.id.btnPost);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_forum);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_forum) {
                startActivity(new Intent(CreatePost.this, Forum.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(CreatePost.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_calculator) {
                startActivity(new Intent(CreatePost.this, Calculator.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_graph) {
                startActivity(new Intent(CreatePost.this, GraphOptions.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_history) {
                startActivity(new Intent(CreatePost.this, History.class));
                overridePendingTransition(0, 0);
                return true;
            } else
                return false;
        });
        btnSelectImage.setOnClickListener(v -> imagePicker());
        btnPost.setOnClickListener(v -> post());
    }

    private void imagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageViewSelected.setImageBitmap(selectedImageBitmap);
                imageViewSelected.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void post() {
        String email = editTextEmail.getText().toString().trim();
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();
        byte[] image = selectedImageBitmap != null ? com.example.ocr2.Utils.getBytes(selectedImageBitmap) : null;

        if (isValidEmail(email)) {
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                Post post = new Post(email, title, content, image);
                FirebaseHelper.addPost(post);
                Toast.makeText(this, "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                emptyField();
            }
        } else {
            wrongEmail();
        }
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void wrongEmail() {
        new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage("Không đúng định dạng email")
                .setPositiveButton("OK", null)
                .show();
    }

    private void emptyField() {
        Toast.makeText(this, "Vui lòng nhập đầy đủ câu hỏi, nội dung và email", Toast.LENGTH_SHORT).show();
    }
}
