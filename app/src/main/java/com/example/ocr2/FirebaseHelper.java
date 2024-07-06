package com.example.ocr2;

import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper extends AppCompatActivity {
    private static final String TAG = "FirebaseHelper";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String POSTS_COLLECTION = "Posts";
    private static final String COMMENTS_COLLECTION = "Comments";

    public static void addPost(Post post) {
        String imageBase64 = Base64.encodeToString(post.getImage(), Base64.DEFAULT);
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("email", post.getEmail());
        postMap.put("title", post.getTitle());
        postMap.put("content", post.getContent());
        postMap.put("image", imageBase64);

        db.collection(POSTS_COLLECTION)
                .add(postMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public static void getAllPosts(final PostCallback callback) {
        db.collection(POSTS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Post> postList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = new Post();
                                post.setId(Integer.parseInt(document.getId()));
                                post.setEmail(document.getString("email"));
                                post.setTitle(document.getString("title"));
                                post.setContent(document.getString("content"));
                                post.setImage(Base64.decode(document.getString("image"), Base64.DEFAULT));
                                postList.add(post);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                        callback.onCallback(postList);
                    }
                });
    }

    public static void getPost(String id, final PostCallback callback) {
        db.collection(POSTS_COLLECTION)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Post post = null;
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            post = new Post();
                            post.setId(Integer.parseInt(document.getId()));
                            post.setEmail(document.getString("email"));
                            post.setTitle(document.getString("title"));
                            post.setContent(document.getString("content"));
                            post.setImage(Base64.decode(document.getString("image"), Base64.DEFAULT));
                        } else {
                            Log.w(TAG, "Error getting document.", task.getException());
                        }
                        callback.onCallback((List<Post>) post);
                    }
                });
    }

    public static void addComment(Comment comment) {
        String imageBase64 = Base64.encodeToString(comment.getImage(), Base64.DEFAULT);
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("postId", comment.getPostId());
        commentMap.put("email", comment.getEmail());
        commentMap.put("content", comment.getContent());
        commentMap.put("image", imageBase64);

        db.collection(COMMENTS_COLLECTION)
                .add(commentMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Comment added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding comment", e);
                    }
                });
    }

    public static void getAllCommentsForPost(int postId, final CommentCallback callback) {
        db.collection(COMMENTS_COLLECTION)
                .whereEqualTo("postId", postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Comment> commentList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Comment comment = new Comment();
                                comment.setId(Integer.parseInt(document.getId()));
                                comment.setPostId(document.getLong("postId").intValue());
                                comment.setEmail(document.getString("email"));
                                comment.setContent(document.getString("content"));
                                comment.setImage(Base64.decode(document.getString("image"), Base64.DEFAULT));
                                commentList.add(comment);
                            }
                        } else {
                            Log.w(TAG, "Error getting comments.", task.getException());
                        }
                        callback.onCallback(commentList);
                    }
                });
    }

    public interface PostCallback {
        void onCallback(List<Post> postList);
    }

    public interface CommentCallback {
        void onCallback(List<Comment> commentList);
    }
}
