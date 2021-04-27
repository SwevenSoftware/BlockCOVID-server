package it.sweven.blockcovid.blockchain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import it.sweven.blockcovid.blockchain.exceptions.ContractNotDeployed;
import it.sweven.blockcovid.blockchain.repositories.BlockchainDeploymentInformationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;

class BlockchainDeploymentInformationServiceTest {
  private BlockchainDeploymentInformationRepository repository;
  private BlockchainDeploymentInformationService service;

  @BeforeEach
  void setUp() {
    repository = mock(BlockchainDeploymentInformationRepository.class);
    service = new BlockchainDeploymentInformationService(repository);
  }

  @Test
  void returnsRepoContent() throws ContractNotDeployed {
    BlockchainDeploymentInformation fakeInfo = mock(BlockchainDeploymentInformation.class);
    when(repository.findByAccountAndNetwork(any(), any()))
        .thenReturn(Optional.ofNullable(fakeInfo));
    assertEquals(fakeInfo, service.getByAccountAndNetwork(mock(Credentials.class), "network"));
  }

  @Test
  void emptyThrowsException() {
    BlockchainDeploymentInformation fakeInfo = mock(BlockchainDeploymentInformation.class);
    when(repository.findByAccountAndNetwork(any(), any())).thenReturn(Optional.empty());
    assertThrows(
        ContractNotDeployed.class,
        () -> service.getByAccountAndNetwork(mock(Credentials.class), "network"));
  }
}
