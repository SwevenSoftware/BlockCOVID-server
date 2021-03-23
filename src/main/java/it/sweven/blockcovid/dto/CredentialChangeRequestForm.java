package it.sweven.blockcovid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CredentialChangeRequestForm {
  @JsonProperty("old_password")
  @NotNull
  private final String oldPassword;

  @JsonProperty("new_password")
  @NotNull
  private final String newPassword;
}
