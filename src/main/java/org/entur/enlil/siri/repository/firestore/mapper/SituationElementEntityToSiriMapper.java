package org.entur.enlil.siri.repository.firestore.mapper;

import java.util.Objects;
import java.util.Optional;
import org.entur.enlil.siri.helpers.DateMapper;
import org.entur.enlil.siri.helpers.SiriObjectFactory;
import org.entur.enlil.siri.repository.firestore.entity.PtSituationElementEntity;
import uk.org.siri.siri21.PtSituationElement;
import uk.org.siri.siri21.ReportTypeEnumeration;
import uk.org.siri.siri21.SeverityEnumeration;
import uk.org.siri.siri21.WorkflowStatusEnumeration;

public class SituationElementEntityToSiriMapper {

  private SituationElementEntityToSiriMapper() {}

  public static PtSituationElement mapToPtSituationElement(PtSituationElementEntity dto) {
    PtSituationElement ptSituationElement = new PtSituationElement();
    ptSituationElement.setCreationTime(
      DateMapper.mapISOStringToZonedDateTime(
        Objects.requireNonNull(dto.getCreationTime())
      )
    );

    ptSituationElement.setParticipantRef(
      SiriObjectFactory.createRequestorRef(dto.getParticipantRef())
    );

    ptSituationElement.setSituationNumber(
      SiriObjectFactory.createSituationNumber(dto.getSituationNumber())
    );

    ptSituationElement.setSource(
      SiriObjectFactory.createSituationSourceStructure(dto.getSource().getSourceType())
    );

    ptSituationElement.setProgress(
      WorkflowStatusEnumeration.fromValue(Objects.requireNonNull(dto.getProgress()))
    );

    ptSituationElement
      .getValidityPeriods()
      .add(
        SiriObjectFactory.createValidityPeriod(
          Objects.requireNonNull(dto.getValidityPeriod().getStartTime()),
          dto.getValidityPeriod().getEndTime()
        )
      );

    ptSituationElement.setUndefinedReason("");

    ptSituationElement.setSeverity(
      SeverityEnumeration.fromValue(Objects.requireNonNull(dto.getSeverity()))
    );

    ptSituationElement.setReportType(
      ReportTypeEnumeration.fromValue(Objects.requireNonNull(dto.getReportType()))
    );

    ptSituationElement
      .getSummaries()
      .add(
        SiriObjectFactory.createDefaultedTextStructure(
          dto.getSummary().getAttributes().getXmlLang(),
          dto.getSummary().getText()
        )
      );

    Optional
      .ofNullable(dto.getDescription())
      .ifPresent(text ->
        ptSituationElement
          .getDescriptions()
          .add(
            SiriObjectFactory.createDefaultedTextStructure(
              text.getAttributes().getXmlLang(),
              text.getText()
            )
          )
      );

    Optional
      .ofNullable(dto.getAdvice())
      .ifPresent(text ->
        ptSituationElement
          .getAdvices()
          .add(
            SiriObjectFactory.createDefaultedTextStructure(
              text.getAttributes().getXmlLang(),
              text.getText()
            )
          )
      );

    ptSituationElement.setAffects(
      SiriObjectFactory.createAffectsScopeStructure(
        Optional
          .ofNullable(dto.getAffects().getNetworks())
          .map(networks -> {
            var lineRef = networks.getAffectedNetwork().getAffectedLine().getLineRef();

            var stopPointRefs = Optional
              .ofNullable(networks.getAffectedNetwork().getAffectedLine().getRoutes())
              .map(routes ->
                routes
                  .getAffectedRoute()
                  .getStopPoints()
                  .getAffectedStopPoint()
                  .stream()
                  .map(affectedStopPoint ->
                    SiriObjectFactory.createAffectedStopPointStructure(
                      affectedStopPoint.getStopPointRef()
                    )
                  )
                  .toList()
              )
              .orElse(null);

            return SiriObjectFactory.createAffectedNetwork(lineRef, stopPointRefs);
          })
          .orElse(null),
        Optional
          .ofNullable(dto.getAffects().getStopPoints())
          .map(stopPoints ->
            stopPoints
              .getAffectedStopPoint()
              .stream()
              .map(affectedStopPoint ->
                SiriObjectFactory.createAffectedStopPointStructure(
                  affectedStopPoint.getStopPointRef()
                )
              )
              .toList()
          )
          .orElse(null),
        Optional
          .ofNullable(dto.getAffects().getVehicleJourneys())
          .map(vehicleJourneys ->
            SiriObjectFactory.createAffectedVehicleJourneyStructure(
              vehicleJourneys
                .getAffectedVehicleJourney()
                .getFramedVehicleJourneyRef()
                .getDataFrameRef(),
              vehicleJourneys
                .getAffectedVehicleJourney()
                .getFramedVehicleJourneyRef()
                .getDatedVehicleJourneyRef(),
              Optional
                .ofNullable(vehicleJourneys.getAffectedVehicleJourney().getRoute())
                .map(route ->
                  route
                    .getStopPoints()
                    .getAffectedStopPoint()
                    .stream()
                    .map(affectedStopPoint ->
                      SiriObjectFactory.createAffectedStopPointStructure(
                        affectedStopPoint.getStopPointRef()
                      )
                    )
                    .toList()
                )
                .orElse(null)
            )
          )
          .orElse(null)
      )
    );

    Optional
      .ofNullable(dto.getInfoLinks())
      .ifPresent(infoLinks ->
        ptSituationElement.setInfoLinks(
          SiriObjectFactory.createInfoLinks(
            SiriObjectFactory.createInfoLinkStructure(
              infoLinks.getInfoLink().getUri(),
              infoLinks.getInfoLink().getLabel()
            )
          )
        )
      );

    return ptSituationElement;
  }
}
