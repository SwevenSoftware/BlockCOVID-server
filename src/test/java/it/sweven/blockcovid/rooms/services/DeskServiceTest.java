package it.sweven.blockcovid.rooms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.dto.DeskInfo;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.DeskNotAvailable;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.repositories.DeskRepository;
import it.sweven.blockcovid.rooms.repositories.RoomRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeskServiceTest {

  private DeskRepository repository;
  private RoomRepository roomRepository;
  private DeskService deskService;

  @BeforeEach
  void setUp() {
    repository = mock(DeskRepository.class);
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    roomRepository = mock(RoomRepository.class);
    deskService = new DeskService(repository, roomRepository);
  }

  @Test
  void addDesk_validArguments() throws DeskNotAvailable {
    DeskInfo providedDesk = mock(DeskInfo.class);
    when(providedDesk.getX()).thenReturn(10);
    when(providedDesk.getY()).thenReturn(35);
    Room requestedRoom = mock(Room.class);
    when(requestedRoom.getId()).thenReturn("roomId");
    when(requestedRoom.getWidth()).thenReturn(20);
    when(requestedRoom.getHeight()).thenReturn(50);
    when(roomRepository.findByName("roomName")).thenReturn(Optional.of(requestedRoom));
    when(repository.findByXAndYAndRoomId(any(), any(), any())).thenReturn(Optional.empty());
    assertEquals(new Desk(10, 35, "roomId"), deskService.addDesk(providedDesk, "roomName"));
  }

  @Test
  void addDesk_deskPositionGreaterThanRoomSize_throwsIllegalArgumentException()
      throws DeskNotAvailable {
    DeskInfo providedDesk = mock(DeskInfo.class);
    when(providedDesk.getX()).thenReturn(40);
    when(providedDesk.getY()).thenReturn(10);
    Room requestedRoom = mock(Room.class);
    when(requestedRoom.getWidth()).thenReturn(80);
    when(requestedRoom.getHeight()).thenReturn(5);
    when(roomRepository.findByName("roomName")).thenReturn(Optional.of(requestedRoom));
    assertThrows(
        IllegalArgumentException.class, () -> deskService.addDesk(providedDesk, "roomName"));
    when(requestedRoom.getWidth()).thenReturn(30);
    when(requestedRoom.getHeight()).thenReturn(20);
    assertThrows(
        IllegalArgumentException.class, () -> deskService.addDesk(providedDesk, "roomName"));
  }

  @Test
  void addDesk_positionAlreadyInUse_throwsDeskNotAvailable() {
    DeskInfo providedDesk = mock(DeskInfo.class);
    when(providedDesk.getX()).thenReturn(20);
    when(providedDesk.getY()).thenReturn(10);
    Room requestedRoom = mock(Room.class);
    when(requestedRoom.getWidth()).thenReturn(30);
    when(requestedRoom.getHeight()).thenReturn(50);
    when(roomRepository.findByName("roomName")).thenReturn(Optional.of(requestedRoom));
    when(repository.findByXAndYAndRoomId(any(), any(), any()))
        .thenReturn(Optional.of(mock(Desk.class)));
    assertThrows(DeskNotAvailable.class, () -> deskService.addDesk(providedDesk, "roomName"));
  }

  @Test
  void getDesksByRoom_validRoom() {
    Room associatedRoom = mock(Room.class);
    when(associatedRoom.getId()).thenReturn("roomId");
    when(roomRepository.findByName("roomName")).thenReturn(Optional.of(associatedRoom));
    List<Desk> expectedDesks = List.of(mock(Desk.class), mock(Desk.class));
    when(repository.findAllByRoomId("roomId")).thenReturn(expectedDesks);
    assertEquals(expectedDesks, deskService.getDesksByRoom("roomName"));
  }

  @Test
  void getDesksByRoom_invalidRoom_throwsRoomNotFoundException() {
    when(roomRepository.findByName("roomName")).thenReturn(Optional.empty());
    assertThrows(RoomNotFoundException.class, () -> deskService.getDesksByRoom("roomName"));
  }

  @Test
  void getDeskByInfosAndRoomName_valid() {
    Desk fakeDesk = mock(Desk.class);
    when(roomRepository.findByName(anyString())).thenReturn(Optional.of(mock(Room.class)));
    when(repository.getByXAndYAndRoomId(any(), any(), any())).thenReturn(Optional.of(fakeDesk));
    assertEquals(fakeDesk, deskService.getDeskByInfoAndRoomName(mock(DeskInfo.class), "roomName"));
  }

  @Test
  void deleteRoomNotFound_ThrowsRoomNotFoundException() {
    when(roomRepository.findByName(anyString())).thenReturn(Optional.empty());
    assertThrows(
        RoomNotFoundException.class,
        () -> deskService.getDeskByInfoAndRoomName(mock(DeskInfo.class), "roomName"));
  }

  @Test
  void deleteDeskNotFound_throwsDeskNotFoundException() {
    when(repository.getByXAndYAndRoomId(any(), any(), any()))
        .thenThrow(new DeskNotFoundException());
    when(roomRepository.findByName(anyString())).thenReturn(Optional.of(mock(Room.class)));
    assertThrows(
        DeskNotFoundException.class,
        () -> deskService.getDeskByInfoAndRoomName(mock(DeskInfo.class), "roomName"));
  }

  @Test
  void deleteDeskById_valid() {
    Desk fakeDesk = mock(Desk.class);
    when(repository.deleteById("idDesk")).thenReturn(Optional.of(fakeDesk));
    assertEquals(fakeDesk, deskService.deleteDeskById("idDesk"));
  }

  @Test
  void deleteDeskById_throwsDeskNotFoundException() {
    when(repository.deleteById(anyString())).thenThrow(new DeskNotFoundException());
    assertThrows(DeskNotFoundException.class, () -> deskService.deleteDeskById("idDesk"));
  }

  @Test
  void update_existingDesk() {
    Desk providedDesk = mock(Desk.class);
    when(providedDesk.getId()).thenReturn("idDesk");
    when(repository.findById("idDesk")).thenReturn(Optional.of(providedDesk));
    AtomicBoolean deskSaved = new AtomicBoolean(false);
    when(repository.save(providedDesk))
        .thenAnswer(
            invocation -> {
              deskSaved.set(true);
              return invocation.getArgument(0);
            });
    assertEquals(providedDesk, deskService.update(providedDesk));
    assertTrue(deskSaved.get());
  }

  @Test
  void update_throwsDeskNotFoundException() {
    when(repository.findById(anyString())).thenThrow(new DeskNotFoundException());
    assertThrows(DeskNotFoundException.class, () -> deskService.update(mock(Desk.class)));
  }

  @Test
  void getDeskByIdValidID() {
    Desk fakeDesk = mock(Desk.class);
    when(repository.findById(anyString())).thenReturn(Optional.of(fakeDesk));
    assertEquals(fakeDesk, deskService.getDeskById("id"));
  }

  @Test
  void nullReturn_throwsDeskNotFoundException() {
    Desk fakeDesk = mock(Desk.class);
    when(repository.findById(anyString())).thenReturn(Optional.empty());
    assertThrows(DeskNotFoundException.class, () -> deskService.getDeskById("id"));
  }
}
