package com.example.ocr2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocr2.Adapter.CommentAdapter;

import java.io.IOException;
import java.util.List;

public class PostDetails extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView textViewPostTitle, textViewPostEmail, textViewPostContent;
    private ImageView imageViewPost;
    private EditText editTextCommentEmail, editTextCommentContent;
    private ImageView imageViewCommentSelected;
    private Bitmap selectedCommentImageBitmap;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;

    private String postId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        textViewPostTitle = findViewById(R.id.PostTitle);
        textViewPostEmail = findViewById(R.id.PostEmail);
        textViewPostContent = findViewById(R.id.PostContent);
        imageViewPost = findViewById(R.id.imageViewPost);

        editTextCommentEmail = findViewById(R.id.CommentEmail);
        editTextCommentContent = findViewById(R.id.CommentContent);
        imageViewCommentSelected = findViewById(R.id.imageCommentSelected);
        recyclerViewComments = findViewById(R.id.BG2);
        Button btnSelectCommentImage = findViewById(R.id.buttonCommentImage);
        Button btnPostComment = findViewById(R.id.buttonPostComment);

        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));

        postId = getIntent().getStringExtra("postId");

        loadPostDetails(postId);
        loadComments(postId);

        btnSelectCommentImage.setOnClickListener(v -> imagePicker());
        btnPostComment.setOnClickListener(v -> postComment());
    }

    private void loadPostDetails(String postId) {
        FirebaseHelper.getPost(postId, new FirebaseHelper.PostCallback() {
            @Override
            public void onCallback(List<Post> postList) {
                if (!postList.isEmpty()) {
                    Post post = postList.get(0);
                    textViewPostTitle.setText(post.getTitle());
                    textViewPostEmail.setText(post.getEmail());
                    textViewPostContent.setText(post.getContent());

                    if (post.getImage() != null) {
                        imageViewPost.setImageBitmap(Utils.getImage(post.getImage()));
                        imageViewPost.setVisibility(View.VISIBLE);
                    } else {
                        imageViewPost.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void loadComments(String postId) {
        FirebaseHelper.getAllCommentsForPost(postId, new FirebaseHelper.CommentCallback() {
            @Override
            public void onCallback(List<Comment> commentList) {
                commentAdapter = new CommentAdapter(PostDetails.this, commentList);
                recyclerViewComments.setAdapter(commentAdapter);
            }
        });
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
                selectedCommentImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageViewCommentSelected.setImageBitmap(selectedCommentImageBitmap);
                imageViewCommentSelected.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void postComment() {
        String email = editTextCommentEmail.getText().toString().trim();
        String content = editTextCommentContent.getText().toString().trim();
        byte[] image = selectedCommentImageBitmap != null ? Utils.getBytes(selectedCommentImageBitmap) : null;

        Comment comment = new Comment(postId, email, content, image);
        FirebaseHelper.addComment(comment);

        loadComments(postId);

        editTextCommentEmail.setText("");
        editTextCommentContent.setText("");
        imageViewCommentSelected.setVisibility(View.GONE);
        selectedCommentImageBitmap = null;
    }
}
