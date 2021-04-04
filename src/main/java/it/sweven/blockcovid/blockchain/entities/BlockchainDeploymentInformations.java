package it.sweven.blockcovid.blockchain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.web3j.crypto.Credentials;

@Data
@AllArgsConstructor
@Getter
public class BlockchainDeploymentInformations {
  Credentials account;
  String address;
}
