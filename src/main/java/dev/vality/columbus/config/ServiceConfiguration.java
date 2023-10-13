package dev.vality.columbus.config;

import dev.vality.columbus.ColumbusServiceSrv;
import dev.vality.columbus.service.GeoIpServiceHandler;
import dev.vality.columbus.service.GeoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Bean
    public ColumbusServiceSrv.Iface eventRepoHandler(GeoService service) {
        return new GeoIpServiceHandler(service);
    }
}
