package com.services.fitbeetles.Interface;

import com.services.fitbeetles.model.FacilitySectionReviews;

import java.util.List;

public interface IFacilityReviewsLoadListener {
    void onFacilityReviewsLoadSuccess(List<FacilitySectionReviews> facilitySectionReviews);
    void onFacilityReviewsLoadFailed(String message);
}
