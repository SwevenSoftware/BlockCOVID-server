package it.sweven.blockcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeskInfo {
  private final Integer id;
  private final Integer x, y;
}
