package com.example.comp9900_commercialize.bean;

import java.io.Serializable;

public class Step implements Serializable {
    public String encodedImage;
    public String stepDescription;

    public Step(){

    }

    public Step(String encodedImage, String stepDescription) {
        this.encodedImage = encodedImage;
        this.stepDescription = stepDescription;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public String getStepDescription() {
        return stepDescription;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }
}
