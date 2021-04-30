package it.sweven.blockcovid.blockchain.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.blockchain.dto.ReportInformation;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

class ReportInformationAssemblerTest {
  private ReportInformationAssembler assembler;

  @BeforeEach
  void setUp() {
    assembler = new ReportInformationAssembler();
  }

  @Test
  void toModel() {
    ReportInformation fakeInfo = mock(ReportInformation.class);
    EntityModel<ReportInformation> model = assembler.toModel(fakeInfo);
    assertTrue(model.hasLink("list_all_reports"));
  }

  @Test
  void collectionModel() {
    List<ReportInformation> fakeInfo = List.of(mock(ReportInformation.class));
    CollectionModel<EntityModel<ReportInformation>> collectionModel =
        assembler.toCollectionModel(fakeInfo);
    assertTrue(collectionModel.hasLink("self"));
  }
}
