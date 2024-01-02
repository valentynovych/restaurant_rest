package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.Address;
import com.restaurant_rest.model.address.AddressAddRequest;
import com.restaurant_rest.model.address.AddressRequest;
import com.restaurant_rest.model.address.AddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressMapper MAPPER = Mappers.getMapper(AddressMapper.class);


    AddressResponse addressToAddressResponse(Address address);

    List<AddressResponse> addressListToAddressResponseList(List<Address> addresses);

    Address addressRequestToAddress(AddressRequest addressRequest);

    AddressRequest addressToAddressRequest(Address address);

    Address addressAddRequestToAddress(AddressAddRequest addressRequest);

    AddressAddRequest addressToAddressAddRequest(Address address);
}
