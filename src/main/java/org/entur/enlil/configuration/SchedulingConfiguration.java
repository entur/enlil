package org.entur.enlil.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Profile("scheduling")
@EnableScheduling
public class SchedulingConfiguration {}
