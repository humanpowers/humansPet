package com.example.humanspet;

public class NoticeboardItem {
    private String userImage;
    private String userName;
    private String uploadImage;
    private int likeCount;
    private int commentCount;
    private String comment;
    private String siName;
    private String doName;
    private String title;

    public NoticeboardItem(String userImage, String userName,String siName,String doName, String uploadImage, int likeCount, int commentCount, String comment,String title) {
        this.userImage = userImage;
        this.userName = userName;
        this.uploadImage = uploadImage;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.comment = comment;
        this.siName = siName;
        this.doName = doName;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSiName() {
        return siName;
    }

    public void setSiName(String siName) {
        this.siName = siName;
    }

    public String getDoName() {
        return doName;
    }

    public void setDoName(String doName) {
        this.doName = doName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUploadImage() {
        return uploadImage;
    }

    public void setUploadImage(String uploadImage) {
        this.uploadImage = uploadImage;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
