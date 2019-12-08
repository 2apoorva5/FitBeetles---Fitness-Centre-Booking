package com.services.fitbeetles.model;

public class Bookmarks {
    String name, headerImage, address;

    public Bookmarks() {
    }

    public Bookmarks(String name, String headerImage, String address) {
        this.name = name;
        this.headerImage = headerImage;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
