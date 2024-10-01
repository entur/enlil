# enlil

> father of [nirgali](https://github.com/entur/nirgali)

> lord of the winds

Backend application for a lightweight realtime deviation system.

Consists of two parts:

1. A GraphQL API for managing situation messages, cancellations and 
extra-journeys
2. A SIRI endpoint for requesting SIRI-SX or SIRI-ET versions of the same 
data

Data is stored in a Firestore database.

## GraphQL

A GraphQL API is available at `/graphl`, to be used by frontend application
(nirgali). It allows authorized users to manage data within their assigned
codespace / organisation.

## SIRI

Situation messages, cancellations and extra-journeys in the Firestore 
database are mapped to SIRI-SX and SIRI-ET after retrieval. A REST-endpoint
(`/siri`) is exposed to request SIRI XML via HTTP POST:

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
