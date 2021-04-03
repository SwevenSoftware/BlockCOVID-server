package it.sweven.blockcovid.rooms.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DeskNotAvailableTest {
  @Test
  void checkErrorMessage() {
    String expectedMessage = "error message";
    assertEquals(expectedMessage, new DeskNotAvailable(expectedMessage).getMessage());
  }
}
