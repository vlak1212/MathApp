package com.example.ocr2;

import android.util.Base64;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference postsRef = db.collection("posts");
    private static final CollectionReference commentsRef = db.collection("comments");

    public static void getAllPosts(final PostCallback callback) {
        postsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Post> postList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = new Post();
                            post.setId(document.getString("id"));
                            post.setTitle(document.getString("title"));
                            post.setEmail(document.getString("email"));
                            post.setContent(document.getString("content"));
                            String imageBase64 = document.getString("image");
                            if (imageBase64 != null && !imageBase64.isEmpty()) {
                                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                                post.setImage(decodedString);
                            }
                            postList.add(post);
                        }
                        callback.onCallback(postList);
                    } else {

                    }
                });
    }


    public static void getPost(String postId, final PostCallback callback) {
        postsRef.whereEqualTo("id", postId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Post> postList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Post post = new Post();
                        post.setId(document.getString("id"));
                        post.setTitle(document.getString("title"));
                        post.setEmail(document.getString("email"));
                        post.setContent(document.getString("content"));
                        String imageBase64 = document.getString("image");
                        if (imageBase64 != null && !imageBase64.isEmpty()) {
                            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                            post.setImage(decodedString);
                        }
                        postList.add(post);
                    }
                    callback.onCallback(postList);
                } else {

                }
            }
        });
    }
    public static void getEmailFromId(String postId, final EmailCallback callback) {
        postsRef.whereEqualTo("id", postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String email = document.getString("email");
                                callback.onCallback(email);
                                return;
                            }
                        } else {
                            callback.onCallback(null);
                        }
                    }
                });
    }
    public static void addPost(Post post) {
        String imageData = convertImageToBase64(post.getImage());
        Map<String, Object> P = new HashMap<>();
        long timestamp = System.currentTimeMillis();
        String postId = post.getId();
        if (postId == null || postId.isEmpty()) {
            postId = postsRef.document().getId();
            post.setId(postId);
        }
        P.put("id", postId);
        P.put("title", post.getTitle());
        P.put("email", post.getEmail());
        P.put("content", post.getContent());
        P.put("image", imageData);
        P.put("timestamp", timestamp);
        postsRef.document(postId).set(P);
    }


    public static void getAllCommentsForPost(String postId, final CommentCallback callback) {
        commentsRef.whereEqualTo("postId", postId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Comment> commentList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Comment comment = new Comment();
                        comment.setPostId(document.getString("postId"));
                        comment.setEmail(document.getString("email"));
                        comment.setContent(document.getString("content"));
                        String imageBase64 = document.getString("image");
                        if (imageBase64 != null && !imageBase64.isEmpty()) {
                            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                            comment.setImage(decodedString);
                        }
                        commentList.add(comment);
                    }
                    callback.onCallback(commentList);
                } else {

                }
            }
        });
    }

    public static void addComment(Comment comment) {
        String imageData = convertImageToBase64(comment.getImage());
        Map<String, Object> C = new HashMap<>();
        C.put("postId", comment.getPostId());
        C.put("email", comment.getEmail());
        C.put("content", comment.getContent());
        C.put("image", imageData);
        commentsRef.add(C);
    }

    public interface PostCallback {
        void onCallback(List<Post> postList);
    }
    public interface EmailCallback {
        void onCallback(String email);
    }
    public interface CommentCallback {
        void onCallback(List<Comment> commentList);
    }

    private static String convertImageToBase64(byte[] imageData) {
        if (imageData != null) {
            return Base64.encodeToString(imageData, Base64.DEFAULT);
        }
        return null;
    }
}
