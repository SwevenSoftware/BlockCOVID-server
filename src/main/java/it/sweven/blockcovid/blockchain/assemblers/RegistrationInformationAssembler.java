package it.sweven.blockcovid.blockchain.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.blockchain.controllers.ListReportsController;
import it.sweven.blockcovid.blockchain.controllers.VerificationController;
import it.sweven.blockcovid.blockchain.dto.RegistrationInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class RegistrationInformationAssembler
    implements RepresentationModelAssembler<
        RegistrationInformation, EntityModel<RegistrationInformation>> {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public EntityModel<RegistrationInformation> toModel(RegistrationInformation entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(ListReportsController.class).listReports(null))
            .withRel("list_all_available_reports"),
        linkTo(methodOn(VerificationController.class).verify(null, "")).withSelfRel());
  }
}
