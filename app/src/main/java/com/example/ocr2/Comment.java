package com.example.ocr2;

public class Comment {

    private String postId;
    private String email;
    private String content;
    private byte[] image;

    public Comment() {}

    public Comment(String postId, String email, String content, byte[] image) {
        this.postId = postId;
        this.email = email;
        this.content = content;
        this.image = image;
    }



    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
