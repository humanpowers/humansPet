package com.example.humanspet;

public class ChattingRoomItem {
    int image;
    String title;
    String content;
    String date;

    public ChattingRoomItem(int image, String title, String content, String date) {
        this.image = image;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
