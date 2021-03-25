package it.sweven.blockcovid.services;

import it.sweven.blockcovid.dto.RoomInfo;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.repositories.DeskRepository;
import it.sweven.blockcovid.repositories.RoomRepository;
import java.util.List;
import javax.management.BadAttributeValueExpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
  private final RoomRepository roomRepository;
  private final DeskRepository deskRepository;

  @Autowired
  RoomService(RoomRepository roomRepository, DeskRepository deskRepository) {
    this.roomRepository = roomRepository;
    this.deskRepository = deskRepository;
  }

  public Room save(Room room) {
    return roomRepository.save(room);
  }

  public Room getByName(String roomName) throws RoomNotFoundException {
    return roomRepository.findRoomByName(roomName).orElseThrow(() -> new RoomNotFoundException());
  }

  public Room createRoom(RoomInfo newRoom) throws BadAttributeValueExpException {
    if (newRoom.getName() == null) throw new BadAttributeValueExpException(newRoom.getName());
    if (newRoom.getOpeningAt() == null)
      throw new BadAttributeValueExpException(newRoom.getOpeningAt());
    if (newRoom.getClosingAt() == null)
      throw new BadAttributeValueExpException(newRoom.getClosingAt());
    if (newRoom.getOpeningDays() == null)
      throw new BadAttributeValueExpException(newRoom.getOpeningDays());
    if (newRoom.getWidth() == null) throw new BadAttributeValueExpException(newRoom.getWidth());
    if (newRoom.getHeight() == null) throw new BadAttributeValueExpException(newRoom.getHeight());
    Room toCreate =
        new Room(
            newRoom.getName(),
            newRoom.getOpeningAt(),
            newRoom.getClosingAt(),
            newRoom.getOpeningDays(),
            newRoom.getWidth(),
            newRoom.getHeight());
    return roomRepository.save(toCreate);
  }

  public List<Room> getAllRooms() {
    return roomRepository.findAll();
  }

  public Room deleteRoomByName(String roomName) throws RoomNotFoundException {
    Room toReturn =
        roomRepository.deleteRoomByName(roomName).orElseThrow(RoomNotFoundException::new);
    deskRepository.deleteAllByRoomId(toReturn.getId());
    return toReturn;
  }
}
