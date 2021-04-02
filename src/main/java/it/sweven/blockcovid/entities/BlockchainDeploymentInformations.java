package it.sweven.blockcovid.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;

@Data
@AllArgsConstructor
@Getter
public class BlockchainDeploymentInformations {
  Credentials account;
  DocumentContract contract;
}
