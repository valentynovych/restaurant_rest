package com.restaurant_rest.service;

import com.restaurant_rest.entity.Address;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.mapper.AddressMapper;
import com.restaurant_rest.model.address.AddressAddRequest;
import com.restaurant_rest.model.address.AddressRequest;
import com.restaurant_rest.model.address.AddressResponse;
import com.restaurant_rest.repositoty.AddressRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepo addressRepo;
    @Mock
    private UserService userService;
    @InjectMocks
    private AddressService addressService;
    private Address address;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        address = new Address();
        address.setId(1L);
        address.setUser(user);
        address.setCity("City");
        address.setStreet("Street 15 on");
        address.setBuilding("14");
        address.setEntrance("1");
        address.setDoorCode("1234");
        address.setFloor("5");
        address.setApartment("234");
        address.setAddressName("My house");

    }

    @Test
    void getAddressById_ifAddressIsPresent() {
        when(addressRepo.findById(address.getId())).thenReturn(Optional.of(address));

        AddressResponse addressById = addressService.getAddressById(address.getId());
        assertNotNull(addressById);
        assertEquals(address.getAddressName(), addressById.getAddressName());
        assertEquals(address.getCity(), addressById.getCity());
    }

    @Test
    void getAddressById_ifAddressIsEmpty() {
        when(addressRepo.findById(address.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                addressService.getAddressById(address.getId()));
    }

    @Test
    void getUserAddresses() {
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        addresses.addAll(List.of(new Address(), new Address(), new Address()));

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(addressRepo.findAllByUser(user)).thenReturn(addresses);

        List<AddressResponse> userAddresses = addressService.getUserAddresses(user.getUsername());
        assertFalse(userAddresses.isEmpty());
        assertEquals(4, userAddresses.size());
        assertEquals(userAddresses.get(0).getAddressName(), address.getAddressName());
    }

    @Test
    void createUserAddress() {
        AddressAddRequest addressRequest = AddressMapper.MAPPER.addressToAddressAddRequest(address);
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(addressRepo.save(any(Address.class))).thenReturn(address);

        Long userAddress = addressService.createUserAddress(user.getUsername(), addressRequest);
        assertEquals(user.getId(), userAddress);

    }

    @Test
    void updateUserAddress_ifAddressByUserIsExist() {
        AddressRequest request = AddressMapper.MAPPER.addressToAddressRequest(address);
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(addressRepo.existsAddressByIdAndUser_Email(address.getId(), user.getEmail())).thenReturn(true);
        when(addressRepo.save(any(Address.class))).thenReturn(address);

        AddressResponse response = addressService.updateUserAddress(address.getId(), request, user.getUsername());
        assertNotNull(response);
        assertEquals(request.getId(), response.getId());
        assertEquals(request.getAddressName(), response.getAddressName());
        assertEquals(request.getCity(), response.getCity());
    }

    @Test
    void updateUserAddress_ifAddressByUserIsNotExist() {
        AddressRequest request = AddressMapper.MAPPER.addressToAddressRequest(address);
        when(addressRepo.existsAddressByIdAndUser_Email(address.getId(), user.getEmail())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                addressService.updateUserAddress(address.getId(), request, user.getUsername()));
    }

    @Test
    void deleteUserAddress_ifAddressByUserIsExistAndSuccessDelete() {
        Long id = address.getId();
        when(addressRepo.existsAddressByIdAndUser_Email(id, user.getEmail())).thenReturn(true);
        when(addressRepo.getAddressById(id)).thenReturn(address);
        when(addressRepo.existsById(id)).thenReturn(false);

        boolean isDeleted = addressService.deleteUserAddress(user.getUsername(), id);
        assertTrue(isDeleted);
    }

    @Test
    void deleteUserAddress_ifAddressByUserIsExistAndErrorDelete() {
        Long id = address.getId();
        when(addressRepo.existsAddressByIdAndUser_Email(id, user.getEmail())).thenReturn(true);
        when(addressRepo.getAddressById(id)).thenReturn(address);
        when(addressRepo.existsById(id)).thenReturn(true);

        boolean isDeleted = addressService.deleteUserAddress(user.getUsername(), id);
        assertFalse(isDeleted);
    }

    @Test
    void deleteUserAddress_ifAddressByUserIsNotExist() {
        Long id = address.getId();
        when(addressRepo.existsAddressByIdAndUser_Email(id, user.getEmail())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                addressService.deleteUserAddress(user.getUsername(), id));
    }
}