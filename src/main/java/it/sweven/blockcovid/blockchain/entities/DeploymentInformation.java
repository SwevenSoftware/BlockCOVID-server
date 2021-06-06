package it.sweven.blockcovid.blockchain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@AllArgsConstructor
@Getter
@ToString
public class DeploymentInformation {
  String account;
  String contract, network;
}
