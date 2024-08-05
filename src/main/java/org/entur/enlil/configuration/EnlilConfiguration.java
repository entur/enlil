package org.entur.enlil.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnlilConfiguration {

    @Value("${enlil.siri.default.producerRef:ENT}")
    private String producerRef;

    public String getProducerRef() {
        return producerRef;
    }
}
