package it.sweven.blockcovid.blockchain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.exceptions.HashNotRegistered;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

class BlockchainServiceTest {
  private DocumentContract contract;
  private BlockchainService service;

  @BeforeEach
  void init() {
    this.contract = mock(DocumentContract.class);
    this.service = new BlockchainService(mock(Web3j.class), mock(ContractGasProvider.class));
  }

  @Test
  void validRegistration() throws Exception {
    TransactionReceipt fakeReceipt = mock(TransactionReceipt.class);
    RemoteFunctionCall fakeCall = mock(RemoteFunctionCall.class);
    RemoteFunctionCall<BigInteger> fakeVerify = mock(RemoteFunctionCall.class);
    BigInteger fakePosition = mock(BigInteger.class);
    FileInputStream fakeInput = mock(FileInputStream.class);
    when(contract.add(any())).thenReturn(fakeCall);
    when(contract.verify(any())).thenReturn(fakeVerify);
    when(fakeCall.send()).thenReturn(fakeReceipt);
    when(fakeVerify.send()).thenReturn(fakePosition);
    when(fakePosition.compareTo(any())).thenReturn(1);
    when(fakeInput.readAllBytes()).thenReturn("test".getBytes(StandardCharsets.UTF_8));
    assertEquals(fakeReceipt, service.registerReport(contract, fakeInput));
  }

  @Test
  void registrationFailsToCallNetwork_throwsException() throws Exception {
    RemoteFunctionCall<TransactionReceipt> fakeCall = mock(RemoteFunctionCall.class);
    FileInputStream fakeInput = mock(FileInputStream.class);
    DocumentContract fakeContract = mock(DocumentContract.class);
    when(contract.add(any())).thenReturn(fakeCall);
    when(fakeCall.send()).thenThrow(new Exception());
    assertThrows(Exception.class, () -> service.registerReport(fakeContract, fakeInput));
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
    assertThrows(HashNotRegistered.class, () -> service.registerReport(contract, fakeInput));
  }
}
