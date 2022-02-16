package dev.vality.columbus.config;

import dev.vality.columbus.service.GeoIpServiceHandler;
import dev.vality.columbus.service.GeoService;
import dev.vality.damsel.geo_ip.GeoIpServiceSrv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Bean
    public GeoIpServiceSrv.Iface eventRepoHandler(GeoService service) {
        return new GeoIpServiceHandler(service);
    }
}
