package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.MainCategory;
import com.restaurant_rest.model.category.MainCategoryShortResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MainCategoryMapper {
    MainCategoryMapper MAPPER = Mappers.getMapper(MainCategoryMapper.class);

    List<MainCategoryShortResponse> categoryListToShortResponseList(List<MainCategory> mainCategories);
    MainCategoryShortResponse categoryToShortResponse(MainCategory mainCategory);
}
