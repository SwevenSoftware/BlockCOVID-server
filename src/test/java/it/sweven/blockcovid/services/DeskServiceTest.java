package it.sweven.blockcovid.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.dto.DeskInfo;
import it.sweven.blockcovid.entities.room.Desk;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.exceptions.DeskNotAvailable;
import it.sweven.blockcovid.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.repositories.DeskRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeskServiceTest {

  private DeskRepository repository;
  private RoomService roomService;
  private DeskService deskService;

  @BeforeEach
  void setUp() {
    repository = mock(DeskRepository.class);
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    roomService = mock(RoomService.class);
    deskService = new DeskService(repository, roomService);
  }

  @Test
  void addDesk_validArguments() throws DeskNotAvailable {
    DeskInfo providedDesk = mock(DeskInfo.class);
    when(providedDesk.getId()).thenReturn(1234);
    when(providedDesk.getX()).thenReturn(10);
    when(providedDesk.getY()).thenReturn(35);
    Room requestedRoom = mock(Room.class);
    when(requestedRoom.getId()).thenReturn("roomId");
    when(requestedRoom.getWidth()).thenReturn(20);
    when(requestedRoom.getHeight()).thenReturn(50);
    when(roomService.getByName("roomName")).thenReturn(requestedRoom);
    when(repository.findByXAndYAndRoomId(any(), any(), any())).thenReturn(Optional.empty());
    when(repository.findByIdAndRoomId(any(), any())).thenReturn(Optional.empty());
    assertEquals(new Desk(1234, 10, 35, "roomId"), deskService.addDesk(providedDesk, "roomName"));
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
    when(roomService.getByName("roomName")).thenReturn(requestedRoom);
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
    when(roomService.getByName("roomName")).thenReturn(requestedRoom);
    when(repository.findByXAndYAndRoomId(any(), any(), any()))
        .thenReturn(Optional.of(mock(Desk.class)));
    assertThrows(DeskNotAvailable.class, () -> deskService.addDesk(providedDesk, "roomName"));
  }

  @Test
  void addDesk_idDeskAlreadyInUse_throwsDeskNotAvailable() {
    DeskInfo providedDesk = mock(DeskInfo.class);
    when(providedDesk.getX()).thenReturn(20);
    when(providedDesk.getY()).thenReturn(10);
    Room requestedRoom = mock(Room.class);
    when(requestedRoom.getWidth()).thenReturn(30);
    when(requestedRoom.getHeight()).thenReturn(50);
    when(roomService.getByName("roomName")).thenReturn(requestedRoom);
    when(repository.findByIdAndRoomId(any(), any())).thenReturn(Optional.of(mock(Desk.class)));
    assertThrows(DeskNotAvailable.class, () -> deskService.addDesk(providedDesk, "roomName"));
  }

  @Test
  void getDesksByRoom_validRoom() {
    Room associatedRoom = mock(Room.class);
    when(associatedRoom.getId()).thenReturn("roomId");
    when(roomService.getByName("roomName")).thenReturn(associatedRoom);
    List<Desk> expectedDesks = List.of(mock(Desk.class), mock(Desk.class));
    when(repository.findAllByRoomId("roomId")).thenReturn(expectedDesks);
    assertEquals(expectedDesks, deskService.getDesksByRoom("roomName"));
  }

  @Test
  void getDesksByRoom_invalidRoom_throwsRoomNotFoundException() {
    when(roomService.getByName("roomName")).thenThrow(new RoomNotFoundException());
    assertThrows(RoomNotFoundException.class, () -> deskService.getDesksByRoom("roomName"));
  }

  @Test
  void deleteDeskByInfosAndRoomName_valid() {
    Desk fakeDesk = mock(Desk.class);
    when(roomService.getByName(anyString())).thenReturn(mock(Room.class));
    when(repository.deleteByXAndYAndRoomId(any(), any(), any())).thenReturn(Optional.of(fakeDesk));
    assertEquals(
        fakeDesk, deskService.deleteDeskByInfosAndRoomName(mock(DeskInfo.class), "roomName"));
  }

  @Test
  void deleteRoomNotFound_ThrowsRoomNotFoundException() {
    when(roomService.getByName(anyString())).thenThrow(new RoomNotFoundException());
    assertThrows(
        RoomNotFoundException.class,
        () -> deskService.deleteDeskByInfosAndRoomName(mock(DeskInfo.class), "roomName"));
  }

  @Test
  void deleteDeskNotFound_throwsDeskNotFoundException() {
    when(repository.deleteByXAndYAndRoomId(any(), any(), any()))
        .thenThrow(new DeskNotFoundException());
    when(roomService.getByName(anyString())).thenReturn(mock(Room.class));
    assertThrows(
        DeskNotFoundException.class,
        () -> deskService.deleteDeskByInfosAndRoomName(mock(DeskInfo.class), "roomName"));
  }
}
