package it.sweven.blockcovid.rooms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.rooms.dto.RoomInfo;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.RoomBuilder;
import it.sweven.blockcovid.rooms.entities.Status;
import it.sweven.blockcovid.rooms.exceptions.RoomNameNotAvailable;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.repositories.DeskRepository;
import it.sweven.blockcovid.rooms.repositories.RoomRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
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
  void createRoom_validRoomInfo() throws BadAttributeValueExpException, RoomNameNotAvailable {
    RoomInfo info =
        new RoomInfo("", LocalTime.now(), LocalTime.now(), Set.of(DayOfWeek.MONDAY), 1, 2);
    Room testRoom = mock(Room.class);
    when(repository.save(any())).thenReturn(testRoom);
    assertEquals(testRoom, service.createRoom(info));
  }

  @Test
  void createRoom_roomNameNotAvailable() {
    RoomInfo info =
        new RoomInfo("roomName", LocalTime.now(), LocalTime.now(), Set.of(DayOfWeek.MONDAY), 1, 2);
    when(repository.findRoomByName("roomName")).thenReturn(Optional.of(mock(Room.class)));
    assertThrows(RoomNameNotAvailable.class, () -> service.createRoom(info));
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
  void updateRoom_validUpdate_noNewName()
      throws BadAttributeValueExpException, RoomNameNotAvailable {
    RoomInfo updateInfo =
        new RoomInfo(null, LocalTime.MIDNIGHT, LocalTime.NOON, Set.of(DayOfWeek.MONDAY), 10, 10);
    Room expected =
        new RoomBuilder()
            .name("name")
            .openingTime(LocalTime.MIDNIGHT)
            .closingTime(LocalTime.MIDNIGHT)
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .width(10)
            .height(10)
            .build();
    Room previousRoom =
        new RoomBuilder()
            .name("name")
            .openingTime(LocalTime.NOON)
            .closingTime(LocalTime.NOON)
            .openingDays(Set.of(DayOfWeek.THURSDAY))
            .width(5)
            .height(5)
            .build();
    when(repository.findRoomByName("name")).thenReturn(Optional.of(previousRoom));
    when(repository.save(expected)).thenReturn(expected);
    assertEquals(expected, service.updateRoom("name", updateInfo));
  }

  @Test
  void updateRoom_validUpdate_sameName()
      throws BadAttributeValueExpException, RoomNameNotAvailable {
    RoomInfo updateInfo =
        new RoomInfo("name", LocalTime.MIDNIGHT, LocalTime.NOON, Set.of(DayOfWeek.MONDAY), 10, 10);
    Room expected =
        new RoomBuilder()
            .name("name")
            .openingTime(LocalTime.MIDNIGHT)
            .closingTime(LocalTime.MIDNIGHT)
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .width(10)
            .height(10)
            .build();
    Room previousRoom = new Room();
    when(repository.findRoomByName("name")).thenReturn(Optional.of(previousRoom));
    when(repository.save(expected)).thenReturn(expected);
    when(repository.save(expected)).thenReturn(expected);
    assertEquals(expected, service.updateRoom("name", updateInfo));
  }

  @Test
  void updateRoom_validUpdate_differentName()
      throws BadAttributeValueExpException, RoomNameNotAvailable {
    RoomInfo updateInfo =
        new RoomInfo(
            "newName", LocalTime.MIDNIGHT, LocalTime.NOON, Set.of(DayOfWeek.MONDAY), 10, 10);
    Room expected =
        new RoomBuilder()
            .name("newName")
            .openingTime(LocalTime.MIDNIGHT)
            .closingTime(LocalTime.MIDNIGHT)
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .width(10)
            .height(10)
            .build();
    Room previousRoom = new Room();
    when(repository.findRoomByName("newName")).thenReturn(Optional.empty());
    when(repository.findRoomByName("previousName")).thenReturn(Optional.of(previousRoom));
    when(repository.save(expected)).thenReturn(expected);
    assertEquals(expected, service.updateRoom("previousName", updateInfo));
  }

  @Test
  void updateRoom_roomNotFound() {
    RoomInfo updateInfo =
        new RoomInfo("name", LocalTime.MIDNIGHT, LocalTime.NOON, Set.of(DayOfWeek.MONDAY), 10, 10);
    when(repository.findRoomByName("name")).thenReturn(Optional.empty());
    assertThrows(RoomNotFoundException.class, () -> service.updateRoom("name", updateInfo));
  }

  @Test
  void updateRoom_roomNameNotAvailable() {
    RoomInfo updateInfo =
        new RoomInfo(
            "newName", LocalTime.MIDNIGHT, LocalTime.NOON, Set.of(DayOfWeek.MONDAY), 10, 10);
    when(repository.findRoomByName("newName")).thenReturn(Optional.of(mock(Room.class)));
    when(repository.findRoomByName("previousName")).thenReturn(Optional.of(mock(Room.class)));
    assertThrows(RoomNameNotAvailable.class, () -> service.updateRoom("previousName", updateInfo));
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
  void setRoomStatus_validChange() {
    Room fakeRoom = mock(Room.class);
    Desk fakeDesk = mock(Desk.class);
    List<Desk> fakeDeskList = List.of(fakeDesk, fakeDesk);
    when(deskRepository.findAllByRoomId(any())).thenReturn(fakeDeskList);
    when(repository.findRoomByName(any())).thenReturn(Optional.of(fakeRoom));
    when(repository.save(any())).thenReturn(fakeRoom);
    assertEquals(fakeRoom, service.setStatus("room", Status.CLEAN));
  }

  @Test
  void setRoomStatus_invalidChange() {
    when(repository.findRoomByName(any())).thenReturn(Optional.ofNullable(null));
    assertThrows(RoomNotFoundException.class, () -> service.setStatus("room", Status.DIRTY));
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
