package it.sweven.blockcovid.reservations.entities;

import java.time.LocalDateTime;
import javax.persistence.GeneratedValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

@Getter
@Setter
public class Reservation {
  @Id
  @GeneratedValue
  @Setter(value = AccessLevel.NONE)
  private Long id;

  private String username, deskId;
  private LocalDateTime start, end;

  @PersistenceConstructor
  Reservation(Long id, String deskId, String username, LocalDateTime start, LocalDateTime end) {
    this.id = id;
    this.deskId = deskId;
    this.username = username;
    this.start = start;
    this.end = end;
  }

  public Reservation(String deskId, String username, LocalDateTime start, LocalDateTime end) {
    this.deskId = deskId;
    this.username = username;
    this.start = start;
    this.end = end;
  }
}
