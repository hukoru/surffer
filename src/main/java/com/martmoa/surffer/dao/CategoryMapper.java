package com.martmoa.surffer.dao;

import com.martmoa.surffer.domain.Category;

import java.util.List;

/**
 * Created by John on 2015-05-15.
 */
public interface CategoryMapper {

    // A01 메뉴 상품 카테고리
    public List<Category> getAllCategory();

    // C02 카테고리
    public List<Category> searchCategoryList(Category category);


}
