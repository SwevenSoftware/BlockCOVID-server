package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import it.sweven.blockcovid.users.entities.User;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class ContractAddressControllerTest {

  private DeploymentInformation deploymentInformation;
  private ContractAddressController controller;

  @BeforeEach
  void setUp() {
    deploymentInformation = mock(DeploymentInformation.class);
    when(deploymentInformation.getContract()).thenReturn("ContractAddress");
    when(deploymentInformation.getAccount()).thenReturn("ContractAccount");
    when(deploymentInformation.getNetwork()).thenReturn("ContractNetwork");
    controller = new ContractAddressController(deploymentInformation);
  }

  @Test
  void HappyPath() {
    EntityModel<DeploymentInformation> info = controller.contractAddress(mock(User.class));
    assertEquals("ContractAddress", Objects.requireNonNull(info.getContent()).getContract());
  }
}
