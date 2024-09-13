package org.entur.enlil.housekeeping;

import java.util.concurrent.TimeUnit;
import org.entur.enlil.repository.SituationElementRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CloseOpenExpiredMessagesScheduler {

  private final SituationElementRepository situationElementRepository;

  public CloseOpenExpiredMessagesScheduler(
    SituationElementRepository situationElementRepository
  ) {
    this.situationElementRepository = situationElementRepository;
  }

  @Scheduled(
    fixedRateString = "${org.entur.enlil.closeOpenExpired.schedule.fixedRate:10}",
    timeUnit = TimeUnit.MINUTES
  )
  public void closeOpenExpiredMessages() {
    situationElementRepository.closeOpenExpiredMessages();
  }
}
