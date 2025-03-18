package com.example.bluetech.service;

import com.example.bluetech.entity.Address;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    Address save(Address address);
    Address add(Address address);
    Address addAddressByIp(String ip) throws JsonProcessingException;
    Optional<Address> findById(String id);
    List<Address> findAll();

}
