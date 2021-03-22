package it.sweven.blockcovid.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DeskNotAvailableTest {
  @Test
  void checkErrorMessage() {
    String expectedMessage = "error message";
    assertEquals(expectedMessage, new DeskNotAvailable(expectedMessage).getMessage());
  }
}
