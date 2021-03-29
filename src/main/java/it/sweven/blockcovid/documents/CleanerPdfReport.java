package it.sweven.blockcovid.documents;

import it.sweven.blockcovid.entities.room.Room;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

public class CleanerPdfReport {
  private PdfReport report;
  private final @Getter(AccessLevel.PROTECTED) ArrayList<Room> rooms;

  public CleanerPdfReport() {
    report = null;
    rooms = new ArrayList<>();
  }

  protected PdfReport createNewPdfReport() {
    return new PdfReport();
  }

  public void addRoom(Room room) {
    rooms.add(room);
  }

  public String save() throws IOException {
    if (report == null) report = createNewPdfReport();
    report.setTableHeader(List.of("Room name", "Status"));
    rooms.forEach(r -> report.addRowTable(List.of(r.getName(), r.getRoomStatus().toString())));
    return report.save();
  }
}
