package com.example.humanspet;

public class WalkingDiaryItem {
    String date;
    String time;
    String calorie;
    String distance;
    String image;

    public WalkingDiaryItem(String date, String time, String calorie, String distance,String image) {
        this.date = date;
        this.time = time;
        this.calorie = calorie;
        this.distance = distance;
        this.image=image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
