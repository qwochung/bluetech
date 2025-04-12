package com.example.bluetech.dto.respone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpLocationResponse {
    private String ip;
    private String hostname;
    private String city;
    private String region;
    private String country;
    private String loc;
    private String postal;
    private String timezone;
    private String org;


    public double[] getLatLong() {
        double[] latLong = new double[]{0,0};
        if (loc != null && loc.contains(",")) {
            String[] locs = loc.split(",");
            latLong[0] = Double.parseDouble(locs[0]);
            latLong[1] = Double.parseDouble(locs[1]);
        }

        return latLong;
    }

}
