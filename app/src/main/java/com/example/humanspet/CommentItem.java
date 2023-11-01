package com.example.humanspet;

public class CommentItem {
    private String name;
    private String image;
    private String comment;

    public CommentItem(String name, String image, String comment) {
        this.name = name;
        this.image = image;
        this.comment = comment;
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
