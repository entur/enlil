package org.entur.enlil.siri.service.filter;

import java.time.ZonedDateTime;
import java.util.function.Predicate;
import uk.org.siri.siri21.PtSituationElement;
import uk.org.siri.siri21.WorkflowStatusEnumeration;

public class OpenExpiredMessagesFilter implements Predicate<PtSituationElement> {

  private final ZonedDateTime latestAllowedDate;

  public OpenExpiredMessagesFilter(ZonedDateTime latestAllowedDate) {
    this.latestAllowedDate = latestAllowedDate;
  }

  @Override
  public boolean test(PtSituationElement ptSituationElement) {
    if (
      WorkflowStatusEnumeration.OPEN.equals(ptSituationElement.getProgress()) &&
      ptSituationElement
        .getValidityPeriods()
        .stream()
        .anyMatch(validityPeriod -> validityPeriod.getEndTime() != null)
    ) {
      return ptSituationElement
        .getValidityPeriods()
        .stream()
        .anyMatch(validityPeriod -> validityPeriod.getEndTime().isAfter(latestAllowedDate)
        );
    } else {
      return true;
    }
  }
}
