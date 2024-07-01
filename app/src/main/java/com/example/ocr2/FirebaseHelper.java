package com.example.ocr2;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class FirebaseHelper extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentComment = "Siz5gD2kJCETmkZQclk3";
    String documentPost = "zl4ATnQSgRM2ZsHiUm2T";
    String collection = "forumData";
    public void addPost(Post post) {

    }
    public List<Post> getAllPosts() {

        return null;
    }

    public Post getPost(int id) {
        return null;
    }

    public void addComment(Comment comment) {

    }

    public List<Comment> getAllCommentsForPost(int postId) {
        return null;
    }

}

