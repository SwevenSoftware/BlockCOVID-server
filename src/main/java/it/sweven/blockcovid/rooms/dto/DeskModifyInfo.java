package it.sweven.blockcovid.rooms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeskModifyInfo {
  private final DeskInfo oldInfo, newInfo;
}
