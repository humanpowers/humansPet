package com.example.humanspet;

public class ChatItem {

    private String image;
    private String name;
    private String message;
    private String date;

    public ChatItem(String image, String name, String message, String date) {
        this.image = image;
        this.name = name;
        this.message = message;
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
