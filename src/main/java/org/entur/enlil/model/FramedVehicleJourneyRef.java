package org.entur.enlil.model;

import com.google.cloud.firestore.annotation.PropertyName;

public class FramedVehicleJourneyRef {

  private String dataFrameRef;

  private String datedVehicleJourneyRef;

  @PropertyName("DataFrameRef")
  public String getDataFrameRef() {
    return dataFrameRef;
  }

  @PropertyName("DataFrameRef")
  public void setDataFrameRef(String dataFrameRef) {
    this.dataFrameRef = dataFrameRef;
  }

  @PropertyName("DatedVehicleJourneyRef")
  public String getDatedVehicleJourneyRef() {
    return datedVehicleJourneyRef;
  }

  @PropertyName("DatedVehicleJourneyRef")
  public void setDatedVehicleJourneyRef(String datedVehicleJourneyRef) {
    this.datedVehicleJourneyRef = datedVehicleJourneyRef;
  }
}
