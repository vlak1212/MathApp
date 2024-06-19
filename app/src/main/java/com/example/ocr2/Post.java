package com.example.ocr2;

public class Post {
    private int id;
    private String title;
    private String content;
    private String email;
    private byte[] image;

    public Post() {}

    public Post(String title, String content, String email, byte[] image) {
        this.title = title;
        this.content = content;
        this.email = email;
        this.image = image;
    }

    public Post(int id, String title, String content, String email, byte[] image) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.email = email;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
