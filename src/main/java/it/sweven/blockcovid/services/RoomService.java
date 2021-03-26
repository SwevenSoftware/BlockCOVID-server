package it.sweven.blockcovid.services;

import it.sweven.blockcovid.dto.RoomInfo;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.room.Status;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.repositories.DeskRepository;
import it.sweven.blockcovid.repositories.RoomRepository;
import java.util.List;
import java.util.Optional;
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

  public Room createRoom(RoomInfo roomInfo) throws BadAttributeValueExpException {
    Room toCreate = new Room();
    toCreate.setName(
        Optional.ofNullable(roomInfo.getName())
            .orElseThrow(() -> new BadAttributeValueExpException(roomInfo)));
    toCreate.setOpeningTime(
        Optional.ofNullable(roomInfo.getOpeningAt())
            .orElseThrow(() -> new BadAttributeValueExpException(roomInfo)));
    toCreate.setClosingTime(
        Optional.ofNullable(roomInfo.getClosingAt())
            .orElseThrow(() -> new BadAttributeValueExpException(roomInfo)));
    toCreate.setOpeningDays(
        Optional.ofNullable(roomInfo.getOpeningDays())
            .orElseThrow(() -> new BadAttributeValueExpException(roomInfo)));
    toCreate.setWidth(
        Optional.ofNullable(roomInfo.getWidth())
            .orElseThrow(() -> new BadAttributeValueExpException(roomInfo)));
    toCreate.setHeight(
        Optional.ofNullable(roomInfo.getHeight())
            .orElseThrow(() -> new BadAttributeValueExpException(roomInfo)));
    return roomRepository.save(toCreate);
  }

  public List<Room> getAllRooms() {
    return roomRepository.findAll();
  }

  public Room setStatus(String roomName, Status status) throws RoomNotFoundException {
    Room toChange = roomRepository.findRoomByName(roomName).orElseThrow(RoomNotFoundException::new);
    deskRepository.findAllByRoomId(toChange.getId()).forEach(desk -> desk.setDeskStatus(status));
    toChange.setRoomStatus(status);
    return roomRepository.save(toChange);
  }

  public Room deleteRoomByName(String roomName) throws RoomNotFoundException {
    Room toReturn =
        roomRepository.deleteRoomByName(roomName).orElseThrow(RoomNotFoundException::new);
    deskRepository.deleteAllByRoomId(toReturn.getId());
    return toReturn;
  }
}
