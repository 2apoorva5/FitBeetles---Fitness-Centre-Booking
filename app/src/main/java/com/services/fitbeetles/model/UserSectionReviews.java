package com.services.fitbeetles.model;

public class UserSectionReviews {
    String facilityName, overallRating, detailedRating;

    public UserSectionReviews() {
    }

    public UserSectionReviews(String facilityName, String overallRating, String detailedRating) {
        this.facilityName = facilityName;
        this.overallRating = overallRating;
        this.detailedRating = detailedRating;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
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
