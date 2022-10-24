package com.example.comp9900_commercialize.bean;

import java.util.ArrayList;
import java.util.List;

public class Collection {

    public List<String> collectionList = new ArrayList<>();

    public Collection(List<String> collectionList) {
        this.collectionList = collectionList;
    }

    public List<String> getCollectionList() {
        return collectionList;
    }

    public void setCollectionList(List<String> collectionList) {
        this.collectionList = collectionList;
    }
    public Collection(){
    }
////
}
