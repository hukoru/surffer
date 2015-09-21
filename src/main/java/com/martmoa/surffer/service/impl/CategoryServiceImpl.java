package com.martmoa.surffer.service.impl;

import com.martmoa.surffer.dao.CategoryMapper;
import com.martmoa.surffer.domain.Category;
import com.martmoa.surffer.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by John on 2015-05-27.
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<HashMap> getAllCategory() {

        List<HashMap> result = new ArrayList<>();
        categoryMapper.getAllCategory().forEach(category ->{
            HashMap<String, Object> map = new HashMap<>();
            map.put("categoryThumImg", category.getCategoryThumImg());
            map.put("categoryName", category.getCategoryName());
            map.put("categoryOrder", category.getCategoryOrder());
            map.put("upCategoryId", category.getUpCategoryId());
            map.put("categoryLevel", category.getCategoryLevel());
            map.put("categoryId", category.getCategoryId());
            result.add(map);
        });

        return result;
    }

    @Override
    public List<HashMap> searchCategoryList(Category category) {

        List<HashMap> result = new ArrayList<>();
        categoryMapper.searchCategoryList(category).forEach(cat ->{
            HashMap<String, Object> map = new HashMap<>();
            map.put("categoryId",  cat.getCategoryId());
            map.put("categoryName", cat.getCategoryName());
            map.put("categoryOrder", cat.getCategoryOrder());
            result.add(map);
        });

        return result;
    }

}
