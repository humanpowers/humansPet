package com.example.humanspet;

public class DiaryInfoItem {
    String image;
    String name;
    String sex;
    String types;
    private boolean selected;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    String birth;
    String kind;
    String registrationNumber;
    String weight;

    public DiaryInfoItem(String  image, String name, String sex, String types, String birth, String kind, String registrationNumber, String weight) {
        this.image = image;
        this.name = name;
        this.sex = sex;
        this.types = types;
        this.birth = birth;
        this.kind = kind;
        this.registrationNumber = registrationNumber;
        this.weight = weight;
    }

    public DiaryInfoItem(String  image, String name) {
        this.image = image;
        this.name = name;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public String  getImage() {
        return image;
    }

    public void setImage(String  image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
