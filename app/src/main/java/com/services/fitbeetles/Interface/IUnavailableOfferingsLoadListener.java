package com.services.fitbeetles.Interface;

import com.services.fitbeetles.model.AvailableAmenities;
import com.services.fitbeetles.model.UnavailableOfferings;

import java.util.List;

public interface IUnavailableOfferingsLoadListener {
    void onUnavailableOfferingsLoadSuccess(List<UnavailableOfferings> unavailableOfferings);
    void onUnavailableOfferingsLoadFailed(String message);
}
