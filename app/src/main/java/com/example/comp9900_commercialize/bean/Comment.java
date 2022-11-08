package com.example.comp9900_commercialize.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Comment {
    public List<itemComment> commentList = new ArrayList<>();
    public List<itemComment> getCommentList() {
        return commentList;
    }
    public Comment(){}

    public Comment(List<itemComment> commentList) {
        this.commentList = commentList;
    }

    public void setCommentList(List<itemComment> commentList) {
        this.commentList = commentList;
    }

    public Comment(itemComment iC){
        this.commentList=new ArrayList<itemComment>(){{
            add(iC);
        }};
    }
    //add
}
