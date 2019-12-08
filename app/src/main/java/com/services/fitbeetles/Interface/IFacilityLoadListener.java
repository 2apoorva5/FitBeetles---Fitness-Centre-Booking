package com.services.fitbeetles.Interface;

import com.services.fitbeetles.model.Facilities;

import java.util.List;

public interface IFacilityLoadListener {
    void onFacilityLoadSuccess(List<Facilities> facilities);
    void onFacilityLoadFailed(String message);
}
