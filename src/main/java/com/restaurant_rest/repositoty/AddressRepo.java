package com.restaurant_rest.repositoty;

import com.restaurant_rest.entity.Address;
import com.restaurant_rest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {
    List<Address> findAllByUser(User user);
    Optional<Address> findByIdAndUser(Long id, User user);
    boolean existsAddressByIdAndUser_Email(Long id, String email);
    Address getAddressById(Long id);
}
