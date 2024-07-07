package com.example.ocr2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

        Post post = new Post(email, title, content, image);
        FirebaseHelper.addPost(post);

        finish();
    }
}
