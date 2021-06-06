package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import it.sweven.blockcovid.users.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractAddressController {
  private final DeploymentInformation deploymentInformation;

  @Autowired
  public ContractAddressController(DeploymentInformation deploymentInformation) {
    this.deploymentInformation = deploymentInformation;
  }

  @GetMapping("api/blockchain/contract")
  @PreAuthorize("#submitter.isAdmin()")
  public EntityModel<String> contractAddress(@AuthenticationPrincipal User submitter) {
    return EntityModel.of(deploymentInformation.getContract());
  }
}
