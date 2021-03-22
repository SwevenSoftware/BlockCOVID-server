package it.sweven.blockcovid.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeskNotAvailable extends Exception {
  public DeskNotAvailable(String message) {
    super(message);
  }
}
