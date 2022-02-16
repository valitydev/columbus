package dev.vality.columbus;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import dev.vality.damsel.base.InvalidRequest;
import dev.vality.damsel.geo_ip.GeoIDInfo;
import dev.vality.damsel.geo_ip.LocationInfo;
import dev.vality.columbus.dao.GeoIpDao;
import dev.vality.columbus.model.CityLocation;
import dev.vality.columbus.model.Lang;
import dev.vality.columbus.service.GeoIpServiceHandler;
import dev.vality.columbus.service.GeoService;
import dev.vality.columbus.util.IpAddressUtils;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

import static dev.vality.damsel.geo_ip.geo_ipConstants.GEO_ID_UNKNOWN;
import static org.junit.Assert.*;

public class GeoServiceTest extends AbstractIntegrationTest {

    public static final Map<String, String> IP_TO_CITY = new HashMap<>();
    public static final String IP_MOSCOW = "94.159.54.234";
    public static final String IP_LONDON = "212.71.235.130";
    public static final int GEOID_KAMIZIAK = 553248;
    public static final int GEOID_MOSCOW = 524901;
    public static final int GEOID_LONDON = 2643743;

    static {
        IP_TO_CITY.put(IP_MOSCOW, "Moscow");
        IP_TO_CITY.put(IP_LONDON, "London");
    }

    @Autowired
    GeoIpDao geoIpDao;

    @Autowired
    GeoService service;

    GeoIpServiceHandler handler;

    @Before
    public void before() {
        handler = new GeoIpServiceHandler(service);
    }

    @Test
    public void testGetLocationException() throws TException {
        try {
            handler.getLocation("null");
            fail("InvalidRequest expected.");
        } catch (InvalidRequest ir) {
            assertEquals("null", ir.getErrors().get(0));
        }
    }

    @Test
    public void testGetLocationNullCity() throws TException {
        LocationInfo info = handler.getLocation("172.233.233.1");

        assertEquals(GEO_ID_UNKNOWN, info.getCityGeoId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testWrongLangException() throws TException {
        final Integer[] ids = {GEOID_KAMIZIAK, GEOID_MOSCOW, 0};
        try {
            Map<Integer, GeoIDInfo> info = handler.getLocationInfo(set(ids), "rublya");
        } catch (InvalidRequest e) {
            assertTrue(!e.getErrors().isEmpty());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetLocationNullValuesInMap() throws TException {
        final int unknown = 0;
        final Integer[] ids = {GEOID_KAMIZIAK, GEOID_MOSCOW, unknown};
        Map<Integer, GeoIDInfo> info = handler.getLocationInfo(set(ids), "ru");

        assertEquals(info.size(), 2);
        assertEquals("Камызяк", info.get(GEOID_KAMIZIAK).city_name);
        assertEquals("Москва", info.get(GEOID_MOSCOW).city_name);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetLocationNullValuesInNameMap() throws TException {
        final int unknown = 0;
        final Integer[] ids = {GEOID_KAMIZIAK, GEOID_MOSCOW, unknown};
        Map<Integer, String> info = handler.getLocationName(set(ids), "ru");

        assertEquals(info.size(), 2);
        assertEquals("Камызяк", info.get(GEOID_KAMIZIAK));
        assertEquals("Москва", info.get(GEOID_MOSCOW));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetLocationUnknownId() throws TException {
        final int unknown = 0;
        final Integer[] ids = {unknown};
        Map<Integer, String> info = handler.getLocationName(set(ids), "ru");
        assertEquals(info.size(), 0);
    }

    @Test
    public void getLocationByIp() throws IOException, GeoIp2Exception {
        for (String ip : IP_TO_CITY.keySet()) {
            CityResponse cityResponse = service.getLocationByIp(IpAddressUtils.convert(ip));
            assertEquals(cityResponse.getCity().getNames().get(Lang.ENG.getValue()), IP_TO_CITY.get(ip));
        }
    }

    @Test
    public void getLocationsByIps() throws TException {
        Map<String, LocationInfo> map = handler.getLocations(IP_TO_CITY.keySet());

        assertEquals(map.size(), 2);
        assertEquals(map.get(IP_LONDON).getCityGeoId(), GEOID_LONDON);
        assertEquals(map.get(IP_MOSCOW).getCityGeoId(), GEOID_MOSCOW);
    }

    private static CityLocation getById(List<CityLocation> list, int id) {
        for (CityLocation cl : list) {
            if (cl.getGeonameId() == id) {
                return cl;
            }
        }

        return null;
    }

    private static Set<Integer> set(Integer[] ids) {
        return new HashSet(Arrays.asList(ids));
    }
}
