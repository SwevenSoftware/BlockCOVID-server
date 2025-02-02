package it.sweven.blockcovid.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
  @Size(min = 8)
  private final String newPassword;
}
