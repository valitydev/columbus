package dev.vality.columbus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.neovisionaries.i18n.CountryCode;
import dev.vality.columbus.ColumbusServiceSrv;
import dev.vality.columbus.InvalidRequest;
import dev.vality.columbus.LocationInfo;
import dev.vality.columbus.util.IpAddressUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static dev.vality.columbus.columbusConstants.GEO_ID_UNKNOWN;

@Slf4j
@RequiredArgsConstructor
public class GeoIpServiceHandler implements ColumbusServiceSrv.Iface {

    private ObjectMapper mapper = new ObjectMapper();
    private static String UNKNOWN = "UNKNOWN";

    private final GeoService service;

    @Override
    public LocationInfo getLocation(String ip) throws InvalidRequest, TException {
        if (!IpAddressUtils.isValid(ip)) {
            throw new InvalidRequest(Collections.singletonList(ip));
        }

        CityResponse cityResponse = null;
        String json = "";
        try {
            cityResponse = service.getLocationByIp(IpAddressUtils.convert(ip));
            json = mapper.writeValueAsString(cityResponse);
        } catch (AddressNotFoundException e) {
            log.info("IP address {} not found", ip);
        } catch (JsonProcessingException e) {
            logAndThrow("CityResponse cannot be converted to JSON.", e);
        } catch (IOException | GeoIp2Exception e) {
            logAndThrow("Unknown IO exception.", e);
        }

        int cityId = GEO_ID_UNKNOWN;
        int countryId = GEO_ID_UNKNOWN;
        if (cityResponse != null && cityResponse.getCity().getGeoNameId() != null) {
            cityId = cityResponse.getCity().getGeoNameId();
        }
        if (cityResponse != null && cityResponse.getCountry().getGeoNameId() != null) {
            countryId = cityResponse.getCountry().getGeoNameId();
        }
        LocationInfo locationInfo = new LocationInfo(cityId, countryId);
        locationInfo.setRawResponse(json);
        return locationInfo;
    }

    @Override
    public Map<String, LocationInfo> getLocations(Set<String> set) throws InvalidRequest, TException {
        List<String> invalidIps = set.stream().filter(ip -> !IpAddressUtils.isValid(ip)).collect(Collectors.toList());
        if (!invalidIps.isEmpty()) {
            throw new InvalidRequest(invalidIps);
        }

        Map<String, LocationInfo> map = new HashMap<>();
        for (String ip : set) {
            map.put(ip, getLocation(ip));
        }

        return map;
    }

    @Override
    public String getLocationIsoCode(String ip) throws InvalidRequest, TException {
        if (!IpAddressUtils.isValid(ip)) {
            throw new InvalidRequest(Collections.singletonList(ip));
        }
        try {
            CityResponse cityResponse = service.getLocationByIp(IpAddressUtils.convert(ip));
            if (cityResponse != null && cityResponse.getCountry().getIsoCode() != null) {
                CountryCode alpha2Code = CountryCode.getByAlpha2Code(cityResponse.getCountry().getIsoCode());
                if (alpha2Code != null) {
                    return alpha2Code.getAlpha3();
                }
            }
        } catch (AddressNotFoundException e) {
            log.info("IP address {} not found", ip);
        } catch (Exception e) {
            logAndThrow("Unknown exception.", e);
        }
        return UNKNOWN;
    }

    private void logAndThrow(String message, Exception e) throws TException {
        log.error(message, e);
        throw new TException(message, e);
    }
}
