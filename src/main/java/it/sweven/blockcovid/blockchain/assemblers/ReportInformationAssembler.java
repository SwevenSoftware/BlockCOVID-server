package it.sweven.blockcovid.blockchain.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.blockchain.controllers.ListReportController;
import it.sweven.blockcovid.blockchain.dto.ReportInformation;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ReportInformationAssembler
    implements RepresentationModelAssembler<ReportInformation, EntityModel<ReportInformation>> {
  @Override
  public EntityModel<ReportInformation> toModel(ReportInformation entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(ListReportController.class).listReports(null)).withRel("list_all_reports"));
  }

  @Override
  public CollectionModel<EntityModel<ReportInformation>> toCollectionModel(
      Iterable<? extends ReportInformation> entities) {
    return CollectionModel.of(
        StreamSupport.stream(entities.spliterator(), true)
            .map(this::toModel)
            .collect(Collectors.toList()),
        linkTo(methodOn(ListReportController.class).listReports(null)).withSelfRel());
  }
}
