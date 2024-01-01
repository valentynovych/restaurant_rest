package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.Subcategory;
import com.restaurant_rest.model.category.SubcategoryShortResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {
    SubcategoryMapper MAPPER = Mappers.getMapper(SubcategoryMapper.class);

    List<SubcategoryShortResponse> subcategoryListToShortResponseList(List<Subcategory> subcategories);

    @Mapping(target = "name", source = "subcategoryName")
    SubcategoryShortResponse subcategoryToShortResponse(Subcategory subcategory);

}
