package it.sweven.blockcovid.blockchain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import it.sweven.blockcovid.blockchain.exceptions.ContractNotDeployed;
import it.sweven.blockcovid.blockchain.repositories.DeploymentInformationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;

class DeploymentInformationServiceTest {
  private DeploymentInformationRepository repository;
  private DeploymentInformationService service;

  @BeforeEach
  void setUp() {
    repository = mock(DeploymentInformationRepository.class);
    service = new DeploymentInformationService(repository);
  }

  @Test
  void returnsRepoContent() throws ContractNotDeployed {
    DeploymentInformation fakeInfo = mock(DeploymentInformation.class);
    when(repository.findByAccountAndNetwork(any(), any()))
        .thenReturn(Optional.ofNullable(fakeInfo));
    assertEquals(fakeInfo, service.getByAccountAndNetwork(mock(Credentials.class), "network"));
  }

  @Test
  void emptyThrowsException() {
    DeploymentInformation fakeInfo = mock(DeploymentInformation.class);
    when(repository.findByAccountAndNetwork(any(), any())).thenReturn(Optional.empty());
    assertThrows(
        ContractNotDeployed.class,
        () -> service.getByAccountAndNetwork(mock(Credentials.class), "network"));
  }
}
