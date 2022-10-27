package com.example.comp9900_commercialize.bean;

import java.util.ArrayList;
import java.util.List;

public class Follow {

    public List<String> followList = new ArrayList<>();

    public Follow(List<String> followList) {
        this.followList = followList;
    }

    public List<String> getFollowList() {
        return followList;
    }

    public void setFollowList(List<String> followList) {
        this.followList = followList;
    }
    public Follow(){
    }

}
