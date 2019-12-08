package com.services.fitbeetles.Interface;

import com.services.fitbeetles.model.UserSectionReviews;

import java.util.List;

public interface IUserReviewsLoadListener {
    void onUserReviewsLoadSuccess(List<UserSectionReviews> userSectionReviews);
    void onUserReviewsLoadFailed(String message);
}
