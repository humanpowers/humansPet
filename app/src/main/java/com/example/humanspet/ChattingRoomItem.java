package com.example.humanspet;

public class ChattingRoomItem {
    String image;
    String title;
    String content;
    String date;
    int read;

    public ChattingRoomItem(String image, String title, String content, String date,int read) {
        this.image = image;
        this.title = title;
        this.content = content;
        this.date = date;
        this.read=read;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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
