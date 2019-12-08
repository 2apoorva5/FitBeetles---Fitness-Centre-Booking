package com.services.fitbeetles.Interface;

import com.services.fitbeetles.model.AvailableOfferings;

import java.util.List;

public interface IAvailableOfferingsLoadListener {
    void onAvailableOfferingsLoadSuccess(List<AvailableOfferings> availableOfferings);
    void onAvailableOfferingsLoadFailed(String message);
}
