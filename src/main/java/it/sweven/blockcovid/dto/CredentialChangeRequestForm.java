package it.sweven.blockcovid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CredentialChangeRequestForm {
  @JsonProperty("old_password")
  private final String oldPassword;

  @JsonProperty("new_password")
  private final String newPassword;
}
