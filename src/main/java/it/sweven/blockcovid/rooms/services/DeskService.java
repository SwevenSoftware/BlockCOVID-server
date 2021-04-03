package it.sweven.blockcovid.rooms.services;

import it.sweven.blockcovid.rooms.dto.DeskInfo;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.DeskNotAvailable;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.repositories.DeskRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DeskService {
  private final DeskRepository deskRepository;
  private final RoomService roomService;

  public DeskService(DeskRepository deskRepository, RoomService roomService) {
    this.deskRepository = deskRepository;
    this.roomService = roomService;
  }

  public Desk addDesk(DeskInfo desk, String roomName)
      throws DeskNotAvailable, RoomNotFoundException {
    Room associatedRoom = roomService.getByName(roomName);
    if (desk.getX() > associatedRoom.getWidth() || desk.getY() > associatedRoom.getHeight())
      throw new IllegalArgumentException("desk position greater than room size");
    if (deskRepository
        .findByXAndYAndRoomId(desk.getX(), desk.getY(), associatedRoom.getId())
        .isPresent()) throw new DeskNotAvailable("Position already used in this room");
    return deskRepository.save(new Desk(desk.getX(), desk.getY(), associatedRoom.getId()));
  }

  public List<Desk> getDesksByRoom(String roomName) {
    String roomId = roomService.getByName(roomName).getId();
    return deskRepository.findAllByRoomId(roomId);
  }

  public Desk deleteDeskByInfosAndRoomName(DeskInfo infos, String roomName)
      throws RoomNotFoundException, DeskNotFoundException {
    return deskRepository
        .deleteByXAndYAndRoomId(infos.getX(), infos.getY(), roomService.getByName(roomName).getId())
        .orElseThrow(DeskNotFoundException::new);
  }
}
