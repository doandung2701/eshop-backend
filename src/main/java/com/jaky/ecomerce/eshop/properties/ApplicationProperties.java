package com.jaky.ecomerce.eshop.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 9:17 PM
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private boolean allowCredentials;
    private List<String> allowedOrigins;
    private List<String> allowedHeaders;
    private List<String> exposedHeaders;
    private List<String> allowedMethods;
}
