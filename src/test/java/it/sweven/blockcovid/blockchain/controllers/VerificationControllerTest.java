package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.assemblers.RegistrationInformationAssembler;
import it.sweven.blockcovid.blockchain.exceptions.InvalidHash;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.users.entities.User;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class VerificationControllerTest {

  private VerificationController controller;
  private SignRegistrationService signRegistrationService;
  private RegistrationInformationAssembler registrationInformationAssembler;

  @BeforeEach
  void setUp() throws InvalidHash, Exception {
    signRegistrationService = mock(SignRegistrationService.class);
    when(signRegistrationService.verifyHash(any())).thenReturn(BigInteger.ZERO);

    registrationInformationAssembler = mock(RegistrationInformationAssembler.class);
    doAnswer(invocationOnMock -> EntityModel.of(invocationOnMock.getArgument(0)))
        .when(registrationInformationAssembler)
        .toModel(any());
    controller =
        new VerificationController(signRegistrationService, registrationInformationAssembler);
  }

  @Test
  void happyPath() {
    String hash = "hash";
    assertTrue(
        Objects.requireNonNull(controller.verify(mock(User.class), hash).getContent())
            .getRegistrationTime()
            .equals(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)));
  }

  @Test
  void HashNotFound() throws InvalidHash, Exception {
    when(signRegistrationService.verifyHash(any())).thenThrow(new InvalidHash());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.verify(mock(User.class), "hash"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void ExceptionThrown() throws InvalidHash, Exception {
    when(signRegistrationService.verifyHash(any())).thenThrow(new Exception());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.verify(mock(User.class), "hash"));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }
}
