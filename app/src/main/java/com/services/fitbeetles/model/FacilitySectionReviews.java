package com.services.fitbeetles.model;

public class FacilitySectionReviews {
    String userName, userPic, overallRating, detailedRating;

    public FacilitySectionReviews() {
    }

    public FacilitySectionReviews(String userName, String userPic, String overallRating, String detailedRating) {
        this.userName = userName;
        this.userPic = userPic;
        this.overallRating = overallRating;
        this.detailedRating = detailedRating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(String overallRating) {
        this.overallRating = overallRating;
    }

    public String getDetailedRating() {
        return detailedRating;
    }

    public void setDetailedRating(String detailedRating) {
        this.detailedRating = detailedRating;
    }
}
