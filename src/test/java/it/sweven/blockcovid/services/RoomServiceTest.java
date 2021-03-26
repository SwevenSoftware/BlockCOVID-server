package it.sweven.blockcovid.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.dto.RoomInfo;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.repositories.DeskRepository;
import it.sweven.blockcovid.repositories.RoomRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoomServiceTest {
  private RoomRepository repository;
  private RoomService service;
  private DeskRepository deskRepository;

  @BeforeEach
  void setUp() {
    repository = mock(RoomRepository.class);
    deskRepository = mock(DeskRepository.class);
    service = new RoomService(repository, deskRepository);
  }

  @Test
  void save() {
    Room testRoom = mock(Room.class);
    when(repository.save(any())).thenReturn(testRoom);
    assertEquals(testRoom, service.save(testRoom));
  }

  @Test
  void getByName() throws RoomNotFoundException {
    Room testRoom = mock(Room.class);
    when(repository.findRoomByName(any())).thenReturn(Optional.of(testRoom));
    assertEquals(testRoom, service.getByName("test"));
  }

  @Test
  void getByName_noRoomWithName_throwsRoomNotFoundException() {
    Room testRoom = mock(Room.class);
    when(repository.findRoomByName(any())).thenReturn(Optional.ofNullable(null));
    assertThrows(RoomNotFoundException.class, () -> service.getByName("test"));
  }

  @Test
  void createRoom_validRoomInfo() throws BadAttributeValueExpException {
    RoomInfo info =
        new RoomInfo("", LocalTime.now(), LocalTime.now(), Set.of(DayOfWeek.MONDAY), 1, 2);
    Room testRoom = mock(Room.class);
    when(repository.save(any())).thenReturn(testRoom);
    assertEquals(testRoom, service.createRoom(info));
  }

  @Test
  void createRoom_invalidRoomName() throws BadAttributeValueExpException {
    RoomInfo info =
        new RoomInfo(null, LocalTime.now(), LocalTime.now(), Collections.emptySet(), 0, 0);
    assertThrows(BadAttributeValueExpException.class, () -> service.createRoom(info));
  }

  @Test
  void createRoom_invalidOpeningTime() throws BadAttributeValueExpException {
    RoomInfo info = new RoomInfo("", null, LocalTime.now(), Collections.emptySet(), 0, 0);
    assertThrows(BadAttributeValueExpException.class, () -> service.createRoom(info));
  }

  @Test
  void createRoom_invalidClosingTime() throws BadAttributeValueExpException {
    RoomInfo info = new RoomInfo("", LocalTime.now(), null, Collections.emptySet(), 0, 0);
    assertThrows(BadAttributeValueExpException.class, () -> service.createRoom(info));
  }

  @Test
  void createRoom_invalidDaysOfWeekSet() throws BadAttributeValueExpException {
    RoomInfo info = new RoomInfo("", LocalTime.now(), LocalTime.now(), null, 0, 0);
    assertThrows(BadAttributeValueExpException.class, () -> service.createRoom(info));
  }

  @Test
  void createRoom_invalidWidth() throws BadAttributeValueExpException {
    RoomInfo info =
        new RoomInfo("", LocalTime.now(), LocalTime.now(), Set.of(DayOfWeek.MONDAY), null, 1);
    assertThrows(BadAttributeValueExpException.class, () -> service.createRoom(info));
  }

  @Test
  void createRoom_invalidHeight() throws BadAttributeValueExpException {
    RoomInfo info =
        new RoomInfo("", LocalTime.now(), LocalTime.now(), Set.of(DayOfWeek.MONDAY), 1, null);
    assertThrows(BadAttributeValueExpException.class, () -> service.createRoom(info));
  }

  @Test
  void getAllRooms() {
    AtomicBoolean repoCalled = new AtomicBoolean(false);
    when(repository.findAll())
        .thenAnswer(
            invocation -> {
              repoCalled.set(true);
              return Collections.emptyList();
            });
    service.getAllRooms();
    assertTrue(repoCalled.get());
  }

  @Test
  void deleteValidRoom() {
    Room fakeRoom = mock(Room.class);
    when(repository.deleteRoomByName(anyString())).thenReturn(Optional.of(fakeRoom));
    assertEquals(fakeRoom, service.deleteRoomByName("room"));
  }

  @Test
  void deleteInvalidRoom_throwsRoomNotFoundException() {
    when(repository.deleteRoomByName(anyString())).thenReturn(Optional.ofNullable(null));
    assertThrows(RoomNotFoundException.class, () -> service.deleteRoomByName("room"));
  }
}
