package it.sweven.blockcovid.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.dto.DeskInfo;
import it.sweven.blockcovid.entities.room.Desk;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.exceptions.DeskNotAvailable;
import it.sweven.blockcovid.repositories.DeskRepository;
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
}
