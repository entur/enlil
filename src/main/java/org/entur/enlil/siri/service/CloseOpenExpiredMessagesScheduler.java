package org.entur.enlil.siri.service;

import java.util.concurrent.TimeUnit;
import org.entur.enlil.siri.repository.SituationElementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CloseOpenExpiredMessagesScheduler {

  Logger logger = LoggerFactory.getLogger(CloseOpenExpiredMessagesScheduler.class);

  private final SituationElementRepository situationElementRepository;

  public CloseOpenExpiredMessagesScheduler(
    SituationElementRepository situationElementRepository
  ) {
    this.situationElementRepository = situationElementRepository;
  }

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
  public void closeOpenExpiredMessages() {
    logger.info("called closeOpenExpiredMessages");
    situationElementRepository.closeOpenExpiredMessages();
  }
}
