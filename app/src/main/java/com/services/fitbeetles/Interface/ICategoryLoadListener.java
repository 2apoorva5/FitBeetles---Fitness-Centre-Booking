package com.services.fitbeetles.Interface;

import com.services.fitbeetles.model.Categories;

import java.util.List;

public interface ICategoryLoadListener {
    void onCategoryLoadSuccess(List<Categories> categories);
    void onCategoryLoadFailed(String message);
}
