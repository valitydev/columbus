package dev.vality.columbus.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import dev.vality.columbus.InvalidRequest;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static dev.vality.columbus.columbusConstants.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class GeoIpServiceHandlerTest {

    private static final String IP = "192.168.100.1";
    private static final String RU = "RU";
    public static final String RUS = "RUS";
    GeoIpServiceHandler geoIpServiceHandler;

    @Mock
    GeoService service;
    @Mock
    Country country;

    @BeforeEach
    public void init() throws IOException, GeoIp2Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(country.getIsoCode()).thenReturn(RU);
        CityResponse result = new CityResponse(new City(), new Continent(), country, new Location(), new MaxMind(),
                new Postal(), country, new RepresentedCountry(), null, null);
        Mockito.when(service.getLocationByIp(any())).thenReturn(result);
        geoIpServiceHandler = new GeoIpServiceHandler(service);
    }

    @Test
    public void getLocationIsoCode() throws TException {
        String locationIsoCode = geoIpServiceHandler.getLocationIsoCode(IP);
        assertEquals(RUS, locationIsoCode);
    }

    @Test
    public void getLocationIsoCodeUnknown() throws TException, IOException, GeoIp2Exception {
        Mockito.when(service.getLocationByIp(any())).thenReturn(null);
        String locationIsoCode = geoIpServiceHandler.getLocationIsoCode(IP);
        assertEquals(UNKNOWN, locationIsoCode);
    }

    @Test
    public void getLocationIsoCodeInvalidRequestTest() {
        assertThrows(InvalidRequest.class, () -> geoIpServiceHandler.getLocationIsoCode("23e23e23e2"));
    }

    @Test
    public void getLocationIsoCodeTExceptionTest() throws IOException, GeoIp2Exception {
        Mockito.when(service.getLocationByIp(any())).thenThrow(new RuntimeException());
        assertThrows(TException.class, () -> geoIpServiceHandler.getLocationIsoCode("23e23e23e2"));
    }
}