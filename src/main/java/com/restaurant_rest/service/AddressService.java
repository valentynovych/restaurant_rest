package com.restaurant_rest.service;

import com.restaurant_rest.entity.Address;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.mapper.AddressMapper;
import com.restaurant_rest.model.address.AddressAddRequest;
import com.restaurant_rest.model.address.AddressRequest;
import com.restaurant_rest.model.address.AddressResponse;
import com.restaurant_rest.repositoty.AddressRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class AddressService {
    private final AddressRepo addressRepo;
    private final UserService userService;

    public AddressResponse getAddressById(Long id) {
        log.info("getAddressById() -> start, with id: " + id);
        Optional<Address> byId = addressRepo.findById(id);
        Address address = byId.orElseThrow(() ->
                new EntityNotFoundException(String.format("Адреси з id: %s не знайдено", id)));
        AddressResponse response = AddressMapper.MAPPER.addressToAddressResponse(address);
        log.info("getAddressById() -> exit, return AddressResponse");
        return response;
    }

    public List<AddressResponse> getUserAddresses(String username) {
        log.info("getUserAddresses() -> start, with username: " + username);
        User userByEmail = userService.getUserByEmail(username);
        List<Address> allByUser = addressRepo.findAllByUser(userByEmail);
        List<AddressResponse> addressResponses =
                AddressMapper.MAPPER.addressListToAddressResponseList(allByUser);
        log.info("getUserAddresses() -> exit, return List<AddressResponse> with size: " + addressResponses.size());
        return addressResponses;
    }

    public Long createUserAddress(String username, AddressAddRequest addressRequest) {
        log.info("createUserAddress() -> start, with id: " + username);
        Address address = AddressMapper.MAPPER.addressAddRequestToAddress(addressRequest);
        User userByEmail = userService.getUserByEmail(username);
        address.setUser(userByEmail);
        Address save = addressRepo.save(address);
        log.info("getAddressById() -> exit, return id new Address: " + save.getId());
        return save.getId();
    }

    public AddressResponse updateUserAddress(Long id, AddressRequest addressRequest, String username) {
        log.info("updateUserAddress() -> start, with id: " + id);

        boolean existsAddressByIdAndUser = addressRepo.existsAddressByIdAndUser_Email(id, username);
        if (!existsAddressByIdAndUser) {
            throw new EntityNotFoundException(
                    String.format("Адреси з id: %s не знайдено", id));
        }

        User user = userService.getUserByEmail(username);
        Address request = AddressMapper.MAPPER.addressRequestToAddress(addressRequest);
        request.setUser(user);
        Address save = addressRepo.save(request);
        AddressResponse response = AddressMapper.MAPPER.addressToAddressResponse(save);
        log.info("updateUserAddress() -> exit, return updated AddressResponse, id: " + save.getId());
        return response;
    }

    public boolean deleteUserAddress(String username, Long id) {
        log.info("deleteUserAddress() -> start, with id: " + id);
        boolean existsAddressByIdAndUser = addressRepo.existsAddressByIdAndUser_Email(id, username);
        if (!existsAddressByIdAndUser) {
            throw new EntityNotFoundException(
                    String.format("Адреси з id: %s не знайдено", id));
        }
        Address address = addressRepo.getAddressById(id);
        addressRepo.delete(address);
        if (!addressRepo.existsById(id)) {
            log.info("deleteUserAddress() -> success delete address with id: " + id);
            return true;
        }
        log.info("deleteUserAddress() -> any error on the delete address with id: " + id);
        return false;
    }
}
