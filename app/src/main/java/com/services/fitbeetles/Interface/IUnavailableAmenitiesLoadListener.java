package com.services.fitbeetles.Interface;

import com.services.fitbeetles.model.UnavailableAmenities;
import com.services.fitbeetles.model.UnavailableOfferings;

import java.util.List;

public interface IUnavailableAmenitiesLoadListener {
    void onUnavailableAmenitiesLoadSuccess(List<UnavailableAmenities> unavailableAmenities);
    void onUnavailableAmenitiesLoadFailed(String message);
}
