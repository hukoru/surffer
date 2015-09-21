package com.martmoa.surffer.domain;

import lombok.Data;

public @Data class Category {

    private static final long serialVersionUID = 1L;

    private String parentCategoryId;

    private int sortOrder;

    private String categoryId;   //카테고리 id
    private String categoryLevel;
    private String upCategoryId;
    private int categoryOrder;
    private String categoryName;

    private String categoryImageUrl;
    private String categoryThumImg;

    // 카테고리별 구매 갯수 => 한 사용자에게서 이때까지 구매한 상품에 카테고리에 종합된 결과를 가져올때 사용
    private int salesAmount;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

}