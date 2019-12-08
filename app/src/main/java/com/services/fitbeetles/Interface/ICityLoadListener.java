package com.services.fitbeetles.Interface;

import com.services.fitbeetles.model.Cities;

import java.util.List;

public interface ICityLoadListener {
    void onCityLoadSuccess(List<Cities> cities);
    void onCityLoadFailed(String message);
}
