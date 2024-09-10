# enlil

> father of [nirgali](https://github.com/entur/nirgali)

> lord of the winds

Backend application for a lightweight realtime deviation system.

Connects to firestore database that belongs to nirgali (see above), to retrieve situation messages,
cancellations and extra-journeys. These are mapped to SIRI-SX and SIRI-ET. A REST-endpoint (`/siri`) is exposed
to request SIRI XML via HTTP POST:

Request for SIRI-SX (SituationExchangeRequest):

    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Siri version="2.0" xmlns="http://www.siri.org.uk/siri" xmlns:ns2="http://www.ifopt.org.uk/acsb" xmlns:ns3="http://www.ifopt.org.uk/ifopt" xmlns:ns4="http://datex2.eu/schema/2_0RC1/2_0">
      <ServiceRequest>
        <RequestTimestamp>2020-02-10T08:57:23.397883+01:00</RequestTimestamp>
        <RequestorRef>ENTUR_DEV</RequestorRef>
        <SituationExchangeRequest version="2.0">
          <RequestTimestamp>2020-02-10T08:57:23.397893+01:00</RequestTimestamp>
          <MessageIdentifier>de353056-466d-44ef-a405-d11311281810</MessageIdentifier>
          <PreviewInterval>PT10H</PreviewInterval>
        </SituationExchangeRequest>
      </ServiceRequest>
    </Siri>

Request for SIRI-ET (EstimatedTimetableRequest):

    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Siri version="2.0" xmlns="http://www.siri.org.uk/siri" xmlns:ns2="http://www.ifopt.org.uk/acsb" xmlns:ns3="http://www.ifopt.org.uk/ifopt" xmlns:ns4="http://datex2.eu/schema/2_0RC1/2_0">
      <ServiceRequest>
        <RequestTimestamp>2019-11-06T14:45:00+01:00</RequestTimestamp>
        <RequestorRef>ENTUR_DEV-1</RequestorRef>
        <EstimatedTimetableRequest version="2.0">
          <RequestTimestamp>2019-11-06T14:45:00+01:00</RequestTimestamp>
          <MessageIdentifier>e11d9efb-ee7b-4a67-847a-a254e813f0da</MessageIdentifier>
        </EstimatedTimetableRequest>
      </ServiceRequest>
    </Siri>
