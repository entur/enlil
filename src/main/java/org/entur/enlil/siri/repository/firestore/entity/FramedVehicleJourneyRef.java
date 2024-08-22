package org.entur.enlil.siri.repository.firestore.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class FramedVehicleJourneyRef {

  @PropertyName("DataFrameRef")
  private String dataFrameRef;

  @PropertyName("DatedVehicleJourneyRef")
  private String datedVehicleJourneyRef;

  public String getDataFrameRef() {
    return dataFrameRef;
  }

  public void setDataFrameRef(String dataFrameRef) {
    this.dataFrameRef = dataFrameRef;
  }

  public String getDatedVehicleJourneyRef() {
    return datedVehicleJourneyRef;
  }

  public void setDatedVehicleJourneyRef(String datedVehicleJourneyRef) {
    this.datedVehicleJourneyRef = datedVehicleJourneyRef;
  }
}
