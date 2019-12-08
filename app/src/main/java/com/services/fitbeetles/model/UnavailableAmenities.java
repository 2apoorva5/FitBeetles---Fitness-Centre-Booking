package com.services.fitbeetles.model;

public class UnavailableAmenities
{
    String name, image;

    public UnavailableAmenities() {
    }

    public UnavailableAmenities(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
