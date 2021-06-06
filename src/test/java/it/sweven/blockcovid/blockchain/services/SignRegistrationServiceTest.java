package it.sweven.blockcovid.blockchain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.exceptions.HashNotRegistered;
import it.sweven.blockcovid.blockchain.exceptions.InvalidHash;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

class SignRegistrationServiceTest {

  private DocumentContract contract;
  private SignRegistrationService service;
  private RemoteFunctionCall<BigInteger> verify;
  private TransactionReceipt receipt;

  @BeforeEach
  void setUp() throws Exception {
    receipt = mock(TransactionReceipt.class);
    RemoteFunctionCall<TransactionReceipt> fakeCall = mock(RemoteFunctionCall.class);
    verify = mock(RemoteFunctionCall.class);
    when(verify.send()).thenReturn(BigInteger.ONE);
    contract = mock(DocumentContract.class);
    when(contract.add(any())).thenReturn(fakeCall);
    when(contract.verify(any())).thenReturn(verify);
    when(verify.send()).thenReturn(BigInteger.ONE);
    when(fakeCall.send()).thenReturn(receipt);
    service = new SignRegistrationService(contract);
  }

  @Test
  void happyPath() throws Exception {
    assertEquals(receipt, service.registerString("hash"));
  }

  @Test
  void zeroMeansNotRegistered() throws Exception {
    when(verify.send()).thenReturn(BigInteger.ZERO);
    assertThrows(HashNotRegistered.class, () -> service.registerString("hash"));
  }

  @Test
  void happyVerificationPath() throws InvalidHash, Exception {
    assertEquals(BigInteger.ONE, service.verifyHash("hash"));
  }

  @Test
  void ZeroMeansNotFound() throws Exception {
    when(verify.send()).thenReturn(BigInteger.ZERO);
    assertThrows(InvalidHash.class, () -> service.verifyHash("hash"));
  }
}
