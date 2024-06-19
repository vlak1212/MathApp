package com.example.ocr2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "forum.db";

    private static final String TABLE_POSTS = "posts";
    private static final String COLUMN_POST_ID = "id";
    private static final String COLUMN_POST_EMAIL = "email";
    private static final String COLUMN_POST_TITLE = "title";
    private static final String COLUMN_POST_CONTENT = "content";
    private static final String COLUMN_POST_IMAGE = "image";

    private static final String TABLE_COMMENTS = "comments";
    private static final String COLUMN_COMMENT_ID = "id";
    private static final String COLUMN_COMMENT_POST_ID = "post_id";
    private static final String COLUMN_COMMENT_EMAIL = "email";
    private static final String COLUMN_COMMENT_CONTENT = "content";
    private static final String COLUMN_COMMENT_IMAGE = "image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS + "("
                + COLUMN_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POST_EMAIL + " TEXT,"
                + COLUMN_POST_TITLE + " TEXT,"
                + COLUMN_POST_CONTENT + " TEXT,"
                + COLUMN_POST_IMAGE + " BLOB" + ")";
        db.execSQL(CREATE_POSTS_TABLE);

        String CREATE_COMMENTS_TABLE = "CREATE TABLE " + TABLE_COMMENTS + "("
                + COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_COMMENT_POST_ID + " INTEGER,"
                + COLUMN_COMMENT_EMAIL + " TEXT,"
                + COLUMN_COMMENT_CONTENT + " TEXT,"
                + COLUMN_COMMENT_IMAGE + " BLOB" + ")";
        db.execSQL(CREATE_COMMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        onCreate(db);
    }

    public void addPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_EMAIL, post.getEmail());
        values.put(COLUMN_POST_TITLE, post.getTitle());
        values.put(COLUMN_POST_CONTENT, post.getContent());
        values.put(COLUMN_POST_IMAGE, post.getImage());

        db.insert(TABLE_POSTS, null, values);
        db.close();
    }

    public List<Post> getAllPosts() {
        List<Post> postList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_POSTS + " ORDER BY " + COLUMN_POST_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Post post = new Post();
                post.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POST_ID)));
                post.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_EMAIL)));
                post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_TITLE)));
                post.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_CONTENT)));
                post.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_POST_IMAGE)));

                postList.add(post);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return postList;
    }

    public Post getPost(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_POSTS, new String[]{COLUMN_POST_ID, COLUMN_POST_EMAIL, COLUMN_POST_TITLE, COLUMN_POST_CONTENT, COLUMN_POST_IMAGE},
                COLUMN_POST_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Post post = new Post(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getBlob(4));

        cursor.close();
        return post;
    }

    public void addComment(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMMENT_POST_ID, comment.getPostId());
        values.put(COLUMN_COMMENT_EMAIL, comment.getEmail());
        values.put(COLUMN_COMMENT_CONTENT, comment.getContent());
        values.put(COLUMN_COMMENT_IMAGE, comment.getImage());

        db.insert(TABLE_COMMENTS, null, values);
        db.close();
    }

    public List<Comment> getAllCommentsForPost(int postId) {
        List<Comment> commentList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_COMMENTS + " WHERE " + COLUMN_COMMENT_POST_ID + " = " + postId + " ORDER BY " + COLUMN_COMMENT_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Comment comment = new Comment();
                comment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_ID)));
                comment.setPostId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_POST_ID)));
                comment.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_EMAIL)));
                comment.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_CONTENT)));
                comment.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_IMAGE)));

                commentList.add(comment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return commentList;
    }
}
