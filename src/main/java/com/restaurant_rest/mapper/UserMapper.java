package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.User;
import com.restaurant_rest.model.user.UserProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "fullName", expression = "java(user.getUserDetails().getFirstName() + \" \" + user.getUserDetails().getLastName())")
    @Mapping(target = "phone", source = "userDetails.phone")
    @Mapping(target = "dateOfBirth", source = "userDetails.dateOfBirth")
    @Mapping(target = "activeBonuses", source = "userDetails.activeBonuses")
    @Mapping(target = "amountOfOrders", source = "totalOrders")
    @Mapping(target = "facebookUsername", source = "userDetails.facebookUsername")
    UserProfileResponse userToUserProfile(User user);
}
