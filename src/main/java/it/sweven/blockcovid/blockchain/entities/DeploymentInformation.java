package it.sweven.blockcovid.blockchain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.web3j.crypto.Credentials;

@Data
@AllArgsConstructor
@Getter
@ToString
public class DeploymentInformation {
  Credentials account;
  String contract, network;
}
