package com.example.ocr2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class CreatePost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long TARGET_IMAGE_SIZE = 900 * 1024; // 900KB

    private EditText editTextEmail, editTextTitle, editTextContent;
    private ImageView imageViewSelected;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        editTextTitle = findViewById(R.id.editTitle);
        editTextEmail = findViewById(R.id.editEmail);
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
                Uri imageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                if (getImageSize(originalBitmap) > MAX_IMAGE_SIZE) {
                    Toast.makeText(this, "Ảnh quá lớn, vui lòng chọn ảnh khác dưới 5MB.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap resizedBitmap = resizeBitmapToMaxSize(originalBitmap, TARGET_IMAGE_SIZE);
                imageViewSelected.setImageBitmap(resizedBitmap);
                imageViewSelected.setVisibility(View.VISIBLE);
                selectedImageBitmap = resizedBitmap;

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể xử lý ảnh. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap resizeBitmapToMaxSize(Bitmap bitmap, long maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resizedBitmap = bitmap;

        int quality = 100;
        while (getImageSize(resizedBitmap) > maxSize && quality > 10) {
            baos.reset();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
            byte[] byteArray = baos.toByteArray();
            resizedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            quality -= 10;
        }

        return resizedBitmap;
    }

    private long getImageSize(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return byteArray.length;
    }

    private String generateId() {
        Random random = new Random();
        StringBuilder idBuilder = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            int digit = random.nextInt(10);
            idBuilder.append(digit);
        }

        for (int i = 0; i < 3; i++) {
            char letter = (char) (random.nextInt(26) + 'A');
            idBuilder.append(letter);
        }

        return idBuilder.toString();
    }

    private void post() {
        String title = editTextTitle.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();
        String id = generateId();
        byte[] image = selectedImageBitmap != null ? com.example.ocr2.Utils.getBytes(selectedImageBitmap) : null;

        if (isValidEmail(email)) {
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                Post post = new Post(id, title, email, content, image);
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
