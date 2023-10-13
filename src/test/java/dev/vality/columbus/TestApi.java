package dev.vality.columbus;

import dev.vality.woody.thrift.impl.http.THSpawnClientBuilder;
import org.apache.thrift.TException;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

@Ignore
public class TestApi {
    @Test(expected = UnsupportedOperationException.class)
    public void test() throws URISyntaxException, TException {
        THSpawnClientBuilder clientBuilder = (THSpawnClientBuilder) new THSpawnClientBuilder()
                .withAddress(new URI("http://localhost:8022/repo"));
        ColumbusServiceSrv.Iface client = clientBuilder.build(ColumbusServiceSrv.Iface.class);
        LocationInfo locationInfo = client.getLocation("94.159.54.234");
        Set<Integer> set = new HashSet<>();
        set.add(553248);
        set.add(524901);
    }
}