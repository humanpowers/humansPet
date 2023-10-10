package com.example.humanspet;

public class DiaryDiaryItem {
    String image;
    String date;
    String title;
    String content;

    public DiaryDiaryItem(String image, String date, String title,String content) {
        this.image = image;
        this.date = date;
        this.title = title;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
