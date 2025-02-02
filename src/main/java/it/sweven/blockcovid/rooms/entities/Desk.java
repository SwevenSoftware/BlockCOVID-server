package it.sweven.blockcovid.rooms.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Document
public class Desk {
  private @Id String id;
  private int x, y;
  private String roomId;
  private Status deskStatus;

  @PersistenceConstructor
  public Desk(String id, int x, int y, String roomId, Status deskStatus) {
    this(x, y, roomId);
    this.id = id;
    this.deskStatus = deskStatus;
  }

  public Desk(int x, int y, String roomId) {
    setX(x);
    setY(y);
    this.roomId = roomId;
    this.deskStatus = Status.CLEAN;
  }

  public void setX(int x) {
    if (x <= 0) throw new IllegalArgumentException("x must be a positive value");
    this.x = x;
  }

  public void setY(int y) {
    if (y <= 0) throw new IllegalArgumentException("x must be a positive value");
    this.y = y;
  }
}
