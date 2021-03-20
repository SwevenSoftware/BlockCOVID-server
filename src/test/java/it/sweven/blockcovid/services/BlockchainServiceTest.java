package it.sweven.blockcovid.services;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.document.Document;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

class BlockchainServiceTest {
  private Document contract;
  private Credentials account;
  private ContractGasProvider gasProvider;
  private Web3j connection;
  private BlockchainService service;

  @BeforeEach
  void init() {
    this.contract = mock(Document.class);
    this.account = mock(Credentials.class);
    this.gasProvider = mock(ContractGasProvider.class);
    this.connection = mock(Web3j.class);
    this.service = new BlockchainService(contract, account, gasProvider, connection);
  }

  @Test
  void runTest() {}
}
