package com.example.comp9900_commercialize.bean;

import java.io.Serializable;

public class Ingredient implements Serializable {

    public String ingredientName;
    public String amount;

    public Ingredient(){

    }

    public Ingredient(String ingredientName, String amount) {
        this.ingredientName = ingredientName;
        this.amount = amount;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
