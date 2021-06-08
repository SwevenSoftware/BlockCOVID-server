package it.sweven.blockcovid.blockchain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegistrationInformation {
  private final LocalDateTime registrationTime;
}
