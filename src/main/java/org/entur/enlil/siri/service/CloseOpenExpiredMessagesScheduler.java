package org.entur.enlil.siri.service;

import java.util.concurrent.TimeUnit;
import org.entur.enlil.siri.repository.SituationElementRepository;
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

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
  public void closeOpenExpiredMessages() {
    situationElementRepository.closeOpenExpiredMessages();
  }
}