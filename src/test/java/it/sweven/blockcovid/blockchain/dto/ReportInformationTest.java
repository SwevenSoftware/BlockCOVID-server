package it.sweven.blockcovid.blockchain.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReportInformationTest {
  @Test
  void constructorWorks() {
    String name = "fakeReportName";
    ReportInformation fakeInfo = new ReportInformation(name);
    assertEquals(name, fakeInfo.getName());
    assertNull(fakeInfo.getCreationDate());
    assertNull(fakeInfo.getRegistrationDate());
  }
}
