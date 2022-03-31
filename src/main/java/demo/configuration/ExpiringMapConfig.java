package demo.configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

@Configuration
public class ExpiringMapConfig {

    @Bean
    public Map<String, String> emailExpiringMap() {
        Map<String, String> valueOperations = ExpiringMap.builder()
                                            .maxSize(1000)
                                            .expirationPolicy(ExpirationPolicy.CREATED)
                                            .expiration(3000, TimeUnit.SECONDS)
                                            .build();
        return valueOperations;
    }


}