package org.entur.enlil.siri.helpers;

/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

import org.entur.enlil.configuration.EnlilConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.org.siri.siri21.EstimatedTimetableDeliveryStructure;
import uk.org.siri.siri21.EstimatedVehicleJourney;
import uk.org.siri.siri21.EstimatedVersionFrameStructure;
import uk.org.siri.siri21.PtSituationElement;
import uk.org.siri.siri21.RequestorRef;
import uk.org.siri.siri21.ServiceDelivery;
import uk.org.siri.siri21.Siri;
import uk.org.siri.siri21.SituationExchangeDeliveryStructure;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

@Service
public class SiriObjectFactory {
    public static final String FALLBACK_SIRI_VERSION = "2.1";

    private final EnlilConfiguration configuration;

    public SiriObjectFactory(@Autowired EnlilConfiguration enlilConfiguration) {
        this.configuration = enlilConfiguration;
    }

    public Siri createSXServiceDelivery(Collection<PtSituationElement> elements) {
        Siri siri = createSiriObject(FALLBACK_SIRI_VERSION);
        ServiceDelivery delivery = createServiceDelivery();
        SituationExchangeDeliveryStructure deliveryStructure = new SituationExchangeDeliveryStructure();
        SituationExchangeDeliveryStructure.Situations situations = new SituationExchangeDeliveryStructure.Situations();
        situations.getPtSituationElements().addAll(elements);
        deliveryStructure.setSituations(situations);
        deliveryStructure.setResponseTimestamp(ZonedDateTime.now());
        delivery.getSituationExchangeDeliveries().add(deliveryStructure);
        siri.setServiceDelivery(delivery);
        return siri;
    }

    public Siri createETServiceDelivery(Collection<EstimatedVehicleJourney> elements) {
        Siri siri = createSiriObject(FALLBACK_SIRI_VERSION);
        ServiceDelivery delivery = createServiceDelivery();
        EstimatedTimetableDeliveryStructure deliveryStructure = new EstimatedTimetableDeliveryStructure();
        deliveryStructure.setVersion(FALLBACK_SIRI_VERSION);
        EstimatedVersionFrameStructure estimatedVersionFrameStructure = new EstimatedVersionFrameStructure();
        estimatedVersionFrameStructure.setRecordedAtTime(ZonedDateTime.now());
        estimatedVersionFrameStructure.getEstimatedVehicleJourneies().addAll(elements);
        deliveryStructure.getEstimatedJourneyVersionFrames().add(estimatedVersionFrameStructure);
        deliveryStructure.setResponseTimestamp(ZonedDateTime.now());

        delivery.getEstimatedTimetableDeliveries().add(deliveryStructure);
        siri.setServiceDelivery(delivery);
        return siri;
    }

    private ServiceDelivery createServiceDelivery() {
        ServiceDelivery delivery = new ServiceDelivery();
        delivery.setResponseTimestamp(ZonedDateTime.now());
        if (configuration != null && configuration.getProducerRef() != null) {
            delivery.setProducerRef(createRequestorRef(configuration.getProducerRef()));
        }
        return delivery;
    }

    private static Siri createSiriObject(@Nonnull String version) {
        Siri siri = new Siri();
        siri.setVersion(version);
        return siri;
    }

    public static RequestorRef createRequestorRef(String value) {
        if(value == null) {
            value = UUID.randomUUID().toString();
        }
        RequestorRef requestorRef = new RequestorRef();
        requestorRef.setValue(value);
        return requestorRef;
    }

}
