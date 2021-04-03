package it.sweven.blockcovid.rooms.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeskTest {

  private Desk desk;

  @BeforeEach
  void setUp() {
    desk = new Desk();
  }

  @Test
  void setX_domain() {
    desk.setX(10);
    assertThrows(IllegalArgumentException.class, () -> desk.setX(0));
    assertThrows(IllegalArgumentException.class, () -> desk.setX(-10));
  }

  @Test
  void setY_domain() {
    desk.setY(10);
    assertThrows(IllegalArgumentException.class, () -> desk.setY(0));
    assertThrows(IllegalArgumentException.class, () -> desk.setY(-10));
  }

  @Test
  void persistentConstructor() {
    new Desk(4, 20, "idRoom");
  }
}
