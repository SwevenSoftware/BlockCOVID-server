package it.sweven.blockcovid.entities.room;

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
  private @Id Integer id;
  private int x, y;
  private String roomId;
  private Status deskStatus;

  @PersistenceConstructor
  public Desk(Integer id, int x, int y, String roomId) {
    this.id = id;
    setX(x);
    setY(y);
    this.roomId = roomId;
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
