package com.example.comp9900_commercialize.models;

import java.io.Serializable;

public class User implements Serializable {

    // The class of an user. It has 2 attributes and corresponding set and get functions.
    // It is used in 'Select user' page, so it has attribute email and type.

    public String name, type, avatar, email, token;
}
