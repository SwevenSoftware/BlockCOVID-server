package it.sweven.blockcovid.entities.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Document
public class Room {
  private @Id @Getter(onMethod_ = @JsonIgnore) @Setter(onMethod_ = @JsonIgnore) @EqualsAndHashCode
      .Include String id;
  private @Indexed @EqualsAndHashCode.Include String name;
  private boolean closed;
  private LocalTime openingTime, closingTime;
  private Set<DayOfWeek> openingDays;
  private @EqualsAndHashCode.Include int width, height;
  private Status roomStatus;

  @PersistenceConstructor
  public Room(
      String id,
      String name,
      boolean closed,
      LocalTime openingTime,
      LocalTime closingTime,
      Set<DayOfWeek> openingDays,
      int width,
      int height,
      Status roomStatus) {
    this.id = id;
    this.name = name;
    this.closed = closed;
    setOpeningTime(openingTime);
    setClosingTime(closingTime);
    setOpeningDays(openingDays);
    setWidth(width);
    setHeight(height);
    setRoomStatus(roomStatus);
  }

  public Room(
      String name,
      boolean closed,
      LocalTime openingTime,
      LocalTime closingTime,
      Set<DayOfWeek> openingDays,
      int width,
      int height,
      Status roomStatus) {
    this.name = name;
    this.closed = closed;
    setOpeningTime(openingTime);
    setClosingTime(closingTime);
    setOpeningDays(openingDays);
    setWidth(width);
    setHeight(height);
    setRoomStatus(roomStatus);
  }

  public void setOpeningTime(LocalTime openingTime) {
    if (openingTime != null && closingTime != null && openingTime.isAfter(closingTime))
      throw new IllegalArgumentException("openingTime shall come before closingTime");
    this.openingTime = openingTime;
  }

  public void setClosingTime(LocalTime closingTime) {
    if (closingTime != null && openingTime != null && closingTime.isBefore(openingTime))
      throw new IllegalArgumentException("closingTime shall come after openingTime");
    this.closingTime = closingTime;
  }

  public void setOpeningDays(Set<DayOfWeek> openingDays) {
    if (openingDays.isEmpty()) throw new IllegalArgumentException("At least one day required");
    this.openingDays = openingDays;
  }

  public void setWidth(int width) {
    if (width <= 0) throw new IllegalArgumentException("width must be a positive value");
    this.width = width;
  }

  public void setHeight(int height) {
    if (height <= 0) throw new IllegalArgumentException("height must be a positive value");
    this.height = height;
  }
}
