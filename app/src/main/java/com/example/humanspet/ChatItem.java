package com.example.humanspet;

public class ChatItem {

    private String image;
    private String name;
    private String message;
    private String date;
    private String time;
    private String read;

    public ChatItem(String image, String name, String message,String time, String date,String read) {
        this.image = image;
        this.name = name;
        this.message = message;
        this.date = date;
        this.time=time;
        this.read=read;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
