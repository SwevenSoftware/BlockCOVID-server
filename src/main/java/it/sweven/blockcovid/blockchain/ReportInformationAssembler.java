package it.sweven.blockcovid.blockchain;

import it.sweven.blockcovid.blockchain.dto.ReportInformation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ReportInformationAssembler
    implements RepresentationModelAssembler<ReportInformation, EntityModel<ReportInformation>> {
  @Override
  public EntityModel<ReportInformation> toModel(ReportInformation entity) {
    return null;
  }

  @Override
  public CollectionModel<EntityModel<ReportInformation>> toCollectionModel(
      Iterable<? extends ReportInformation> entities) {
    return RepresentationModelAssembler.super.toCollectionModel(entities);
  }
}
