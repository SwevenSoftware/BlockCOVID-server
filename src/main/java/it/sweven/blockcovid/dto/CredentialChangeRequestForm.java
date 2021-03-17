package it.sweven.blockcovid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.sweven.blockcovid.entities.user.Credentials;

public class CredentialChangeRequestForm {
  @JsonProperty("old_credentials")
  private final Credentials oldCredentials;

  @JsonProperty("new_credentials")
  private final Credentials newCredentials;

  CredentialChangeRequestForm(Credentials oldCredentials, Credentials newCredentials) {
    this.oldCredentials = oldCredentials;
    this.newCredentials = newCredentials;
  }

  public Credentials getOldCredentials() {
    return oldCredentials;
  }

  public Credentials getNewCredentials() {
    return newCredentials;
  }
}
