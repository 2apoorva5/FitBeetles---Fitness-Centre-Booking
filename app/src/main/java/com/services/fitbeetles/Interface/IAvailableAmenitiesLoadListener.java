package com.services.fitbeetles.Interface;

import com.services.fitbeetles.model.AvailableAmenities;

import java.util.List;

public interface IAvailableAmenitiesLoadListener {
    void onAvailableAmenitiesLoadSuccess(List<AvailableAmenities> availableAmenities);
    void onAvailableAmenitiesLoadFailed(String message);
}
