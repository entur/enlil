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

package org.entur.enlil.siri.service;

import org.entur.enlil.repository.EstimatedVehicleJourneyRepository;
import org.entur.enlil.siri.helpers.SiriObjectFactory;
import org.entur.enlil.siri.mapper.EstimatedVehicleJourneyEntityToSiriMapper;
import org.springframework.stereotype.Service;
import uk.org.siri.siri21.Siri;

@Service
public class EstimatedTimetableDeliveryService {

  private final EstimatedVehicleJourneyRepository estimatedVehicleJourneyRepository;
  private final SiriObjectFactory siriObjectFactory;

  public EstimatedTimetableDeliveryService(
    EstimatedVehicleJourneyRepository estimatedVehicleJourneyRepository,
    SiriObjectFactory siriObjectFactory
  ) {
    this.estimatedVehicleJourneyRepository = estimatedVehicleJourneyRepository;
    this.siriObjectFactory = siriObjectFactory;
  }

  public Siri getEstimatedTimetableDelivery() {
    return siriObjectFactory.createETServiceDelivery(
      estimatedVehicleJourneyRepository
        .getAllEstimatedVehicleJourneys()
        .map(EstimatedVehicleJourneyEntityToSiriMapper::mapToEstimatedVehicleJourney)
        .toList()
    );
  }
}
