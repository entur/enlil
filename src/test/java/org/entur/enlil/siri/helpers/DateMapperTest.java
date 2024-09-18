package org.entur.enlil.siri.helpers;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DateMapperTest {

  @Test
  void testMapZonedDateTimeToString() {
    Clock clock = Mockito.mock(Clock.class);
    Mockito.when(clock.instant()).thenReturn(Instant.parse("2023-09-19T19:19:00Z"));
    Mockito.when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    Assertions.assertEquals(
      "2023-09-19T19:19:00Z",
      DateMapper.mapZonedDateTimeToString(ZonedDateTime.now(clock))
    );
  }
}
