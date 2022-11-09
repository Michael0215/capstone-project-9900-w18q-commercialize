package com.example.comp9900_commercialize.bean;

public class LastFeed {

    public String latestTime;

    public LastFeed() {
    }

    public LastFeed(String recipeId) {
        this.latestTime = recipeId;
    }

    public String getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(String latestTime) {
        this.latestTime = latestTime;
    }
}
