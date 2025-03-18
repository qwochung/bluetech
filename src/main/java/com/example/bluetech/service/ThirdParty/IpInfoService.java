package com.example.bluetech.service.ThirdParty;

import com.example.bluetech.dto.IpLocationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IpInfoService {

    private final String API_URL = "https://ipinfo.io/";


    public IpLocationResponse getIpInfo(String ip)   {

//        Handle for test environment
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "42.112.113.212";
        }



        String url = API_URL + ip + "/json";
        RestTemplate restTemplate = new RestTemplate();
       try {
           String response = restTemplate.getForObject(url, String.class);
           ObjectMapper objectMapper = new ObjectMapper();
           return objectMapper.readValue(response, IpLocationResponse.class);
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }



    }

}
