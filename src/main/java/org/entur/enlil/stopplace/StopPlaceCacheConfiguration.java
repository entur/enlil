package org.entur.enlil.stopplace;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StopPlaceCacheConfiguration {

  /**
   * Stop place attributes change rarely. Entries are roughly 200 bytes, so a
   * full cache is about 10 MB against a 2 GB heap.
   */
  @Bean("stopPlaceCache")
  Cache<String, StopPlaceRecord> stopPlaceCache(
    @Value("${enlil.stop-places.cache.stop-place-ttl-minutes:60}") long ttlMinutes,
    @Value("${enlil.stop-places.cache.stop-place-max-size:50000}") long maxSize
  ) {
    return Caffeine
      .newBuilder()
      .expireAfterWrite(Duration.ofMinutes(ttlMinutes))
      .maximumSize(maxSize)
      .build();
  }

  /**
   * Municipality and county names effectively never change, and there are only a
   * few hundred of them, so this never evicts in practice.
   */
  @Bean("topographicNameCache")
  Cache<String, String> topographicNameCache(
    @Value("${enlil.stop-places.cache.topographic-ttl-hours:24}") long ttlHours,
    @Value("${enlil.stop-places.cache.topographic-max-size:5000}") long maxSize
  ) {
    return Caffeine
      .newBuilder()
      .expireAfterWrite(Duration.ofHours(ttlHours))
      .maximumSize(maxSize)
      .build();
  }
}
