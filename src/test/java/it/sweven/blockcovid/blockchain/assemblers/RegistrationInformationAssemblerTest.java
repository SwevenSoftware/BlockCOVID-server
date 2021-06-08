package it.sweven.blockcovid.blockchain.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.blockchain.dto.RegistrationInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class RegistrationInformationAssemblerTest {

  private RegistrationInformationAssembler assembler;

  @BeforeEach
  void setUp() {
    assembler = new RegistrationInformationAssembler();
  }

  @Test
  void containsContent() {
    RegistrationInformation fakeInfo = mock(RegistrationInformation.class);
    EntityModel<RegistrationInformation> entityModel = assembler.toModel(fakeInfo);
    assertEquals(entityModel.getContent(), fakeInfo);
  }

  @Test
  void containsNecessaryLinks() {
    RegistrationInformation fakeInfo = mock(RegistrationInformation.class);
    EntityModel<RegistrationInformation> entityModel = assembler.toModel(fakeInfo);
    assertTrue(entityModel.hasLink("self"));
    assertTrue(entityModel.hasLink("list_all_available_reports"));
  }
}
