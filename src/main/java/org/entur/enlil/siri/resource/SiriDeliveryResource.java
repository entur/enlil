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

package org.entur.enlil.siri.resource;

import org.entur.enlil.siri.error.InvalidServiceRequestException;
import org.entur.enlil.siri.service.EstimatedTimetableDeliveryService;
import org.entur.enlil.siri.service.SituationExchangeDeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.org.siri.siri21.Siri;

@RestController
@RequestMapping("/siri")
public class SiriDeliveryResource {

  private final SituationExchangeDeliveryService situationExchangeDeliveryService;
  private final EstimatedTimetableDeliveryService estimatedTimetableDeliveryService;

  public SiriDeliveryResource(
    SituationExchangeDeliveryService situationExchangeDeliveryService,
    EstimatedTimetableDeliveryService estimatedTimetableDeliveryService
  ) {
    this.situationExchangeDeliveryService = situationExchangeDeliveryService;
    this.estimatedTimetableDeliveryService = estimatedTimetableDeliveryService;
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping(
    consumes = MediaType.APPLICATION_XML_VALUE,
    produces = MediaType.APPLICATION_XML_VALUE
  )
  public Siri requestServiceDelivery(@RequestBody Siri request) {
    var serviceRequest = request.getServiceRequest();

    if (serviceRequest != null) {
      if (!serviceRequest.getEstimatedTimetableRequests().isEmpty()) {
        return estimatedTimetableDeliveryService.getEstimatedTimetableDelivery();
      } else if (!serviceRequest.getSituationExchangeRequests().isEmpty()) {
        return situationExchangeDeliveryService.getSituationExchangeDelivery();
      }
    }

    throw new InvalidServiceRequestException();
  }

  @ExceptionHandler(InvalidServiceRequestException.class)
  public String handleInvalidServiceRequestException(InvalidServiceRequestException ex) {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Message><Body>Invalid ServiceRequest</Body></Message></Response>";
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public String handleHttpMessageNotReadableException(
    HttpMessageNotReadableException ex
  ) {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Message><Body>Invalid XML</Body></Message></Response>";
  }
}
