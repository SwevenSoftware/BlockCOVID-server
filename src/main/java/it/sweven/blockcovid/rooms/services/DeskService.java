package it.sweven.blockcovid.rooms.services;

import it.sweven.blockcovid.rooms.dto.DeskInfo;
import it.sweven.blockcovid.rooms.dto.NewDeskInfo;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.DeskNotAvailable;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.repositories.DeskRepository;
import it.sweven.blockcovid.rooms.repositories.RoomRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DeskService {
  private final DeskRepository deskRepository;
  private final RoomRepository roomRepository;

  public DeskService(DeskRepository deskRepository, RoomRepository roomRepository) {
    this.deskRepository = deskRepository;
    this.roomRepository = roomRepository;
  }

  public Desk addDesk(NewDeskInfo desk, String roomName)
      throws DeskNotAvailable, RoomNotFoundException {
    Room associatedRoom =
        roomRepository.findByName(roomName).orElseThrow(RoomNotFoundException::new);
    if (desk.getX() > associatedRoom.getWidth() || desk.getY() > associatedRoom.getHeight())
      throw new IllegalArgumentException("desk position greater than room size");
    if (deskRepository
        .findByXAndYAndRoomId(desk.getX(), desk.getY(), associatedRoom.getId())
        .isPresent()) throw new DeskNotAvailable("Position already used in this room");
    return deskRepository.save(new Desk(desk.getX(), desk.getY(), associatedRoom.getId()));
  }

  public Desk update(Desk desk) throws DeskNotFoundException {
    deskRepository.findById(desk.getId()).orElseThrow(DeskNotFoundException::new);
    return deskRepository.save(desk);
  }

  public List<Desk> getDesksByRoom(String roomName) throws RoomNotFoundException {
    String roomId =
        roomRepository.findByName(roomName).orElseThrow(RoomNotFoundException::new).getId();
    return deskRepository.findAllByRoomId(roomId);
  }

  public Desk getDeskByInfoAndRoomName(DeskInfo infos, String roomName)
      throws RoomNotFoundException, DeskNotFoundException {
    return deskRepository
        .getByXAndYAndRoomId(
            infos.getX(),
            infos.getY(),
            roomRepository.findByName(roomName).orElseThrow(RoomNotFoundException::new).getId())
        .orElseThrow(DeskNotFoundException::new);
  }

  public Desk deleteDeskById(String id) throws DeskNotFoundException {
    return deskRepository.deleteById(id).orElseThrow(DeskNotFoundException::new);
  }

  public Desk getDeskById(String deskId) throws DeskNotFoundException {
    return deskRepository.findById(deskId).orElseThrow(DeskNotFoundException::new);
  }
}
