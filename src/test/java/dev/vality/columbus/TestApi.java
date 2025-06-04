package dev.vality.columbus;

import dev.vality.woody.thrift.impl.http.THSpawnClientBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
public class TestApi {
    @Test
    public void test() throws URISyntaxException {
        assertThrows(UnsupportedOperationException.class, () -> {
            THSpawnClientBuilder clientBuilder = (THSpawnClientBuilder) new THSpawnClientBuilder()
                    .withAddress(new URI("http://localhost:8022/repo"));
            ColumbusServiceSrv.Iface client = clientBuilder.build(ColumbusServiceSrv.Iface.class);
            LocationInfo locationInfo = client.getLocation("94.159.54.234");
            Set<Integer> set = new HashSet<>();
            set.add(553248);
            set.add(524901);
        });
    }
}