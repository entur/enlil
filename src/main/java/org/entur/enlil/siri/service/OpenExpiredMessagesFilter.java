package org.entur.enlil.siri.service;

import uk.org.siri.siri21.PtSituationElement;
import uk.org.siri.siri21.WorkflowStatusEnumeration;

import java.time.ZonedDateTime;
import java.util.function.Predicate;

public class OpenExpiredMessagesFilter implements Predicate<PtSituationElement> {

    private final ZonedDateTime latestAllowedDate;

    public OpenExpiredMessagesFilter(ZonedDateTime latestAllowedDate) {
        this.latestAllowedDate = latestAllowedDate;
    }

    @Override
    public boolean test(PtSituationElement ptSituationElement) {
        if (WorkflowStatusEnumeration.OPEN.equals(ptSituationElement.getProgress())
                && ptSituationElement.getValidityPeriods().stream().anyMatch(validityPeriod -> validityPeriod.getEndTime() != null)) {
            return ptSituationElement.getValidityPeriods().stream().anyMatch(validityPeriod -> validityPeriod.getEndTime().isAfter(latestAllowedDate));
        } else {
            return true;
        }
    }
}
