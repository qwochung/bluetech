package com.example.bluetech.repository;

import com.example.bluetech.entity.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AddressRepository extends MongoRepository<Address, String> {

}
