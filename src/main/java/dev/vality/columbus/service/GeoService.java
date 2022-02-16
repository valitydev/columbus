package dev.vality.columbus.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import dev.vality.columbus.dao.GeoIpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
public class GeoService {

    @Autowired
    GeoIpDao geoIpDao;

    public CityResponse getLocationByIp(InetAddress ipAddress) throws IOException, GeoIp2Exception {
        return geoIpDao.getLocationInfoByIp(ipAddress);
    }
}
