package com.trackcovid19.model;

public class CasesCount {

  private Integer totalActiveCases;
  private Integer totalRecoveredCases;
  private Integer totalDeceasedCases;

  public Integer getTotalActiveCases() {
    return totalActiveCases;
  }

  public void setTotalActiveCases(Integer totalActiveCases) {
    this.totalActiveCases = totalActiveCases;
  }

  public Integer getTotalRecoveredCases() {
    return totalRecoveredCases;
  }

  public void setTotalRecoveredCases(Integer totalRecoveredCases) {
    this.totalRecoveredCases = totalRecoveredCases;
  }

  public Integer getTotalDeceasedCases() {
    return totalDeceasedCases;
  }

  public void setTotalDeceasedCases(Integer totalDeceasedCases) {
    this.totalDeceasedCases = totalDeceasedCases;
  }
}
