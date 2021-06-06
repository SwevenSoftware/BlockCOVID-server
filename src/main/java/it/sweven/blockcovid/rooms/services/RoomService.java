package it.sweven.blockcovid.rooms.services;

import it.sweven.blockcovid.rooms.dto.RoomInfo;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.RoomBuilder;
import it.sweven.blockcovid.rooms.entities.Status;
import it.sweven.blockcovid.rooms.exceptions.RoomNameNotAvailable;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.repositories.DeskRepository;
import it.sweven.blockcovid.rooms.repositories.RoomRepository;
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

  protected Room save(Room room) {
    return roomRepository.save(room);
  }

  public Room updateRoom(String roomName, RoomInfo newInfo)
      throws RoomNameNotAvailable, RoomNotFoundException {
    if (newInfo.getName() != null
        && !roomName.equals(newInfo.getName())
        && roomRepository.findRoomByName(newInfo.getName()).isPresent())
      throw new RoomNameNotAvailable();

    Room room = getByName(roomName);
    Optional.ofNullable(newInfo.getName()).ifPresent(room::setName);
    Optional.ofNullable(newInfo.getOpeningAt()).ifPresent(room::setOpeningTime);
    Optional.ofNullable(newInfo.getClosingAt()).ifPresent(room::setClosingTime);
    Optional.ofNullable(newInfo.getOpeningDays()).ifPresent(room::setOpeningDays);
    Optional.ofNullable(newInfo.getWidth()).ifPresent(room::setWidth);
    Optional.ofNullable(newInfo.getHeight()).ifPresent(room::setHeight);

    return save(room);
  }

  public Room getByName(String roomName) throws RoomNotFoundException {
    return roomRepository.findRoomByName(roomName).orElseThrow(RoomNotFoundException::new);
  }

  public Room createRoom(RoomInfo roomInfo)
      throws BadAttributeValueExpException, RoomNameNotAvailable {
    if (roomRepository.findRoomByName(roomInfo.getName()).isPresent())
      throw new RoomNameNotAvailable();
    Room toCreate =
        new RoomBuilder()
            .name(roomInfo.getName())
            .openingTime(roomInfo.getOpeningAt())
            .closingTime(roomInfo.getClosingAt())
            .openingDays(roomInfo.getOpeningDays())
            .width(roomInfo.getWidth())
            .height(roomInfo.getHeight())
            .roomStatus(Status.DIRTY)
            .build();

    return roomRepository.save(toCreate);
  }

  public List<Room> getAllRooms() {
    return roomRepository.findAll();
  }

  public Room setStatus(String roomName, Status status, String username)
      throws RoomNotFoundException {
    Room toChange = roomRepository.findRoomByName(roomName).orElseThrow(RoomNotFoundException::new);
    deskRepository
        .findAllByRoomId(toChange.getId())
        .forEach(
            desk -> {
              desk.setDeskStatus(status);
              deskRepository.save(desk);
            });
    toChange.setRoomStatus(status);
    if (status == Status.CLEAN) toChange.setLastCleaner(username);
    return roomRepository.save(toChange);
  }

  public Room deleteRoomByName(String roomName) throws RoomNotFoundException {
    Room toReturn =
        roomRepository.deleteRoomByName(roomName).orElseThrow(RoomNotFoundException::new);
    deskRepository.deleteAllByRoomId(toReturn.getId());
    return toReturn;
  }
}
