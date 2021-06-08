package it.sweven.blockcovid.blockchain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@AllArgsConstructor
@Setter
@EqualsAndHashCode
public class ReportInformation {
  @Id private final String name;
  @JsonIgnore private final String path;
  private final LocalDateTime creationDate;
  private LocalDateTime registrationDate;
  private String hash;
  private String transactionHash;
  private Boolean registered;
}
