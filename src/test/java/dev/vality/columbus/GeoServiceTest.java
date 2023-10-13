package dev.vality.columbus;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import dev.vality.columbus.dao.GeoIpDao;
import dev.vality.columbus.model.CityLocation;
import dev.vality.columbus.model.Lang;
import dev.vality.columbus.service.GeoIpServiceHandler;
import dev.vality.columbus.service.GeoService;
import dev.vality.columbus.util.IpAddressUtils;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

import static dev.vality.columbus.columbusConstants.GEO_ID_UNKNOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = ColumbusApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class GeoServiceTest {

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
