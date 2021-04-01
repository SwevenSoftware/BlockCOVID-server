package it.sweven.blockcovid.entities.room;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import javax.management.BadAttributeValueExpException;

public class RoomBuilder {
  private String name;
  private LocalTime openingTime;
  private LocalTime closingTime;
  private Set<DayOfWeek> openingDays;
  private Integer width;
  private Integer height;
  private Status status = Status.CLEAN;
  private boolean closed;

  public RoomBuilder name(String name) throws BadAttributeValueExpException {
    if (name != null) this.name = name;
    else throw new BadAttributeValueExpException(name);
    return this;
  }

  public RoomBuilder openingTime(LocalTime openingTime) throws BadAttributeValueExpException {
    if (openingTime != null) this.openingTime = openingTime;
    else throw new BadAttributeValueExpException(openingTime);
    return this;
  }

  public RoomBuilder closingTime(LocalTime closingTime) throws BadAttributeValueExpException {
    if (closingTime != null) this.closingTime = closingTime;
    else throw new BadAttributeValueExpException(closingTime);
    return this;
  }

  public RoomBuilder openingDays(Set<DayOfWeek> openingDays) throws BadAttributeValueExpException {
    if (openingDays != null) this.openingDays = openingDays;
    else throw new BadAttributeValueExpException(openingDays);
    return this;
  }

  public RoomBuilder width(Integer width) throws BadAttributeValueExpException {
    if (width != null) this.width = width;
    else throw new BadAttributeValueExpException(width);
    return this;
  }

  public RoomBuilder height(Integer height) throws BadAttributeValueExpException {
    if (height != null) this.height = height;
    else throw new BadAttributeValueExpException(height);
    return this;
  }

  public RoomBuilder closed(boolean closed) {
    this.closed = closed;
    return this;
  }

  public RoomBuilder roomStatus(Status status) throws BadAttributeValueExpException {
    if (status != null) this.status = status;
    else throw new BadAttributeValueExpException(status);
    return this;
  }

  public Room build() throws BadAttributeValueExpException {
    if (name == null
        || openingTime == null
        || closingTime == null
        || openingDays == null
        || width == null
        || height == null) throw new BadAttributeValueExpException(null);

    return new Room(name, closed, openingTime, closingTime, openingDays, width, height, status);
  }
}
