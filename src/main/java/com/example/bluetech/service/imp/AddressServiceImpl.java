package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.dto.IpLocationResponse;
import com.example.bluetech.entity.Address;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.AddressRepository;
import com.example.bluetech.service.AddressService;
import com.example.bluetech.service.ThirdParty.IpInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;


    @Autowired
    private IpInfoService ipInfoService;

    @Override
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public Address add(Address address) {

        //TODO: Handle logic here
        return addressRepository.save(address);
    }

    @Override
    public Address addAddressByIp(String ip) throws JsonProcessingException {
        IpLocationResponse ipLocationResponse = ipInfoService.getIpInfo(ip);
        if (ipLocationResponse == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        Address address = Address.createByIp(ipLocationResponse);

        return address;
    }

    @Override
    public Optional<Address> findById(String id) {
        return addressRepository.findById(id);
    }

    @Override
    public List<Address> findAll() {
        return addressRepository.findAll();
    }
}
