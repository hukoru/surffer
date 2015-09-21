package com.martmoa.surffer.service;

import com.martmoa.surffer.domain.Category;

import java.util.HashMap;
import java.util.List;

public interface CategoryService {

    public List<HashMap> getAllCategory();

    public List<HashMap> searchCategoryList(Category category);
}
