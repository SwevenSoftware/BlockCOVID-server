package it.sweven.blockcovid.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.exceptions.HashNotRegistered;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.document.Document;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

class BlockchainServiceTest {
  private Document contract;
  private Credentials account;
  private ContractGasProvider gasProvider;
  private Web3j connection;
  private BlockchainService service;
  private Logger log;

  @BeforeEach
  void init() {
    this.contract = mock(Document.class);
    this.account = mock(Credentials.class);
    this.gasProvider = mock(ContractGasProvider.class);
    this.connection = mock(Web3j.class);
    this.log = mock(Logger.class);
    this.service = new BlockchainService(contract, account, gasProvider, connection, log);
  }

  @Test
  void validRegistration() throws Exception {
    TransactionReceipt fakeReceipt = mock(TransactionReceipt.class);
    RemoteFunctionCall<TransactionReceipt> fakeCall = mock(RemoteFunctionCall.class);
    RemoteFunctionCall<BigInteger> fakeVerify = mock(RemoteFunctionCall.class);
    BigInteger fakePosition = mock(BigInteger.class);
    FileInputStream fakeInput = mock(FileInputStream.class);
    when(contract.add(any())).thenReturn(fakeCall);
    when(fakeCall.send()).thenReturn(fakeReceipt);
    when(contract.verify(any())).thenReturn(fakeVerify);
    when(fakeVerify.send()).thenReturn(fakePosition);
    when(fakePosition.compareTo(any())).thenReturn(1);
    when(fakeInput.readAllBytes()).thenReturn("test".getBytes(StandardCharsets.UTF_8));
    assertEquals(fakeReceipt, service.registerReport(fakeInput));
  }

  @Test
  void registrationFailsToCallNetwork_throwsException() throws Exception {
    RemoteFunctionCall<TransactionReceipt> fakeCall = mock(RemoteFunctionCall.class);
    FileInputStream fakeInput = mock(FileInputStream.class);
    when(contract.add(any())).thenReturn(fakeCall);
    when(fakeCall.send()).thenThrow(new Exception());
    assertThrows(Exception.class, () -> service.registerReport(fakeInput));
  }

  @Test
  void invalidInsertion() throws Exception {
    TransactionReceipt fakeReceipt = mock(TransactionReceipt.class);
    RemoteFunctionCall<TransactionReceipt> fakeCall = mock(RemoteFunctionCall.class);
    RemoteFunctionCall<BigInteger> fakeVerify = mock(RemoteFunctionCall.class);
    BigInteger fakePosition = mock(BigInteger.class);
    FileInputStream fakeInput = mock(FileInputStream.class);
    when(contract.add(any())).thenReturn(fakeCall);
    when(fakeCall.send()).thenReturn(fakeReceipt);
    when(contract.verify(any())).thenReturn(fakeVerify);
    when(fakeVerify.send()).thenReturn(fakePosition);
    when(fakePosition.compareTo(any())).thenReturn(0);
    when(fakeInput.readAllBytes()).thenReturn("test".getBytes(StandardCharsets.UTF_8));
    assertThrows(HashNotRegistered.class, () -> service.registerReport(fakeInput));
  }
}
