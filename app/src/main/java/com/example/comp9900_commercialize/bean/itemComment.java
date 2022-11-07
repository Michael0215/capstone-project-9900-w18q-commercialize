package com.example.comp9900_commercialize.bean;

import android.graphics.Bitmap;

public class itemComment {
    public String avatar;
    public String comment;
    public String date;
    public String username;

    public itemComment(String avatar, String comment, String date, String username) {
        this.avatar = avatar;
        this.comment = comment;
        this.date = date;
        this.username = username;
    }
    public itemComment(){
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
