package it.sweven.blockcovid.blockchain.dto;

import java.nio.file.attribute.FileTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportInformation {
  private final String name;
  private final FileTime creationDate;
  private final FileTime registrationDate;

  public ReportInformation(String name) {
    this.name = name;
    this.creationDate = null;
    this.registrationDate = null;
  }
}
