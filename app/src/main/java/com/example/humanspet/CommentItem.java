package com.example.humanspet;

public class CommentItem {
    private String name;
    private String image;
    private String comment;
    private boolean push;

    public CommentItem(String name, String image, String comment,boolean push) {
        this.name = name;
        this.image = image;
        this.comment = comment;
        this.push=push;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
