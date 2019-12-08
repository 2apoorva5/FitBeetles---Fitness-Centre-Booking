package com.services.fitbeetles.model;

public class Facilities {
    String name, headerImage, address, contact, openOrClosed, latitude, longitude,
           galleryImage1, galleryImage2, galleryImage3, galleryImage4, galleryImage5, galleryImage6,
           timing1, timing2, timing3, timing4, timing5, timing6, timing7;

    float reviews;

    public Facilities() {
    }

    public Facilities(String name, String headerImage, String address, float reviews) {
        this.name = name;
        this.headerImage = headerImage;
        this.address = address;
        this.reviews = reviews;
    }

    public Facilities(String name, String headerImage, String address, String contact,
                      float reviews, String openOrClosed, String latitude, String longitude,
                      String galleryImage1, String galleryImage2, String galleryImage3,
                      String galleryImage4, String galleryImage5, String galleryImage6,
                      String timing1, String timing2, String timing3, String timing4,
                      String timing5, String timing6, String timing7) {
        this.name = name;
        this.headerImage = headerImage;
        this.address = address;
        this.contact = contact;
        this.reviews = reviews;
        this.openOrClosed = openOrClosed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.galleryImage1 = galleryImage1;
        this.galleryImage2 = galleryImage2;
        this.galleryImage3 = galleryImage3;
        this.galleryImage4 = galleryImage4;
        this.galleryImage5 = galleryImage5;
        this.galleryImage6 = galleryImage6;
        this.timing1 = timing1;
        this.timing2 = timing2;
        this.timing3 = timing3;
        this.timing4 = timing4;
        this.timing5 = timing5;
        this.timing6 = timing6;
        this.timing7 = timing7;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public float getReviews() {
        return reviews;
    }

    public void setReviews(float reviews) {
        this.reviews = reviews;
    }

    public String getOpenOrClosed() {
        return openOrClosed;
    }

    public void setOpenOrClosed(String openOrClosed) {
        this.openOrClosed = openOrClosed;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getGalleryImage1() {
        return galleryImage1;
    }

    public void setGalleryImage1(String galleryImage1) {
        this.galleryImage1 = galleryImage1;
    }

    public String getGalleryImage2() {
        return galleryImage2;
    }

    public void setGalleryImage2(String galleryImage2) {
        this.galleryImage2 = galleryImage2;
    }

    public String getGalleryImage3() {
        return galleryImage3;
    }

    public void setGalleryImage3(String galleryImage3) {
        this.galleryImage3 = galleryImage3;
    }

    public String getGalleryImage4() {
        return galleryImage4;
    }

    public void setGalleryImage4(String galleryImage4) {
        this.galleryImage4 = galleryImage4;
    }

    public String getGalleryImage5() {
        return galleryImage5;
    }

    public void setGalleryImage5(String galleryImage5) {
        this.galleryImage5 = galleryImage5;
    }

    public String getGalleryImage6() {
        return galleryImage6;
    }

    public void setGalleryImage6(String galleryImage6) {
        this.galleryImage6 = galleryImage6;
    }

    public String getTiming1() {
        return timing1;
    }

    public void setTiming1(String timing1) {
        this.timing1 = timing1;
    }

    public String getTiming2() {
        return timing2;
    }

    public void setTiming2(String timing2) {
        this.timing2 = timing2;
    }

    public String getTiming3() {
        return timing3;
    }

    public void setTiming3(String timing3) {
        this.timing3 = timing3;
    }

    public String getTiming4() {
        return timing4;
    }

    public void setTiming4(String timing4) {
        this.timing4 = timing4;
    }

    public String getTiming5() {
        return timing5;
    }

    public void setTiming5(String timing5) {
        this.timing5 = timing5;
    }

    public String getTiming6() {
        return timing6;
    }

    public void setTiming6(String timing6) {
        this.timing6 = timing6;
    }

    public String getTiming7() {
        return timing7;
    }

    public void setTiming7(String timing7) {
        this.timing7 = timing7;
    }
}
