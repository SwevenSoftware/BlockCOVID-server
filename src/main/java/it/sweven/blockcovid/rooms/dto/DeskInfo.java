package it.sweven.blockcovid.rooms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeskInfo {
  private final String id;
  private final Integer x, y;
}
