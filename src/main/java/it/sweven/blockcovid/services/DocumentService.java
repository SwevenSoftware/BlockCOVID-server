package it.sweven.blockcovid.services;

import it.sweven.blockcovid.documents.PdfReport;
import it.sweven.blockcovid.entities.room.Room;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.management.BadAttributeValueExpException;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
  private final String DESTINATION_DIR = "reports";

  public byte[] generateCleanerReport(List<Room> rooms) throws IOException {
    LocalDateTime timestamp = LocalDateTime.now();
    String destination = initializeReport(timestamp);
    PdfReport report = new PdfReport(destination);
    report
        .setTitle("Cleaner Report")
        .setTimestamp(timestamp)
        .setHeaderTable(List.of("Room name", "Status"));
    rooms.forEach(r -> report.addRowTable(List.of(r.getName(), r.getRoomStatus().toString())));
    try {
      report.create();
    } catch (BadAttributeValueExpException e) {
      throw new IOException();
    }
    InputStream inputStream = new FileInputStream(destination);
    return inputStream.readAllBytes();
  }

  protected String initializeReport(LocalDateTime timestamp) throws IOException {
    String destination =
        pathReport(timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
    if (!Files.exists(Path.of(DESTINATION_DIR + "/")))
      Files.createDirectory(Path.of(DESTINATION_DIR));
    Files.createFile(Path.of(destination));
    return destination;
  }

  protected String pathReport(String id) {
    return DESTINATION_DIR + "/Report_" + id + ".pdf";
  }
}
