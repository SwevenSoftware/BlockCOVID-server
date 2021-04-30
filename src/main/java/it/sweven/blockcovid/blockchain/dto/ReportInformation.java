package it.sweven.blockcovid.blockchain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportInformation {
  private final String name;
  private final LocalDateTime creationDate;
  private final LocalDateTime registrationDate;

  public ReportInformation(String name) {
    this.name = name;
    this.creationDate = null;
    this.registrationDate = null;
  }
}
