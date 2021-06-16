package it.sweven.blockcovid.blockchain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import it.sweven.blockcovid.users.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Reports")
@SecurityRequirement(name = "bearer")
public class ContractAddressController {
  private final DeploymentInformation deploymentInformation;

  @Autowired
  public ContractAddressController(DeploymentInformation deploymentInformation) {
    this.deploymentInformation = deploymentInformation;
  }

  @GetMapping("api/blockchain/contract")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "returns the contract where the documents signs are saved on the blockchain")
  })
  @PreAuthorize("#submitter.isAdmin()")
  public EntityModel<DeploymentInformation> contractAddress(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter) {
    return EntityModel.of(deploymentInformation);
  }
}
