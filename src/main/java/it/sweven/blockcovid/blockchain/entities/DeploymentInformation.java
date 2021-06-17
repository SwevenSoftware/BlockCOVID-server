package it.sweven.blockcovid.blockchain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@AllArgsConstructor
@Getter
@ToString
public class DeploymentInformation {
  @JsonIgnore String account;
  String contract;
  @JsonIgnore String network;
}
