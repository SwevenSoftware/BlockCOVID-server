package it.sweven.blockcovid.blockchain.entities;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@AllArgsConstructor
@Setter
public class ReportInformation {
  @Id private final String name;
  private final String path;
  private final LocalDateTime creationDate;
  private LocalDateTime registrationDate;
  private String hash;
  private String transactionHash;
  private boolean registered;

  public ReportInformation(String name) {
    this.name = name;
    this.path = null;
    this.creationDate = null;
    this.registrationDate = null;
    this.hash = null;
    this.transactionHash = null;
    this.registered = false;
  }
}
