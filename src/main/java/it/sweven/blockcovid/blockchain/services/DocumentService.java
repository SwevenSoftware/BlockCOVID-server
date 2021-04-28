package it.sweven.blockcovid.blockchain.services;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

import it.sweven.blockcovid.blockchain.documents.PdfReport;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.rooms.entities.Room;
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
import org.springframework.util.DigestUtils;

@Service
public class DocumentService {
  private final String DESTINATION_DIR = "reports";

  public String generateCleanerReport(List<Room> rooms) throws IOException {
    LocalDateTime timestamp = LocalDateTime.now();
    String destination = initializeReport(timestamp);
    PdfReport report = createNewReport();
    report
        .setTitle("Cleaner Report")
        .setTimestamp(timestamp)
        .setHeaderTable(List.of("Room name", "Status"));
    rooms.forEach(r -> report.addRowTable(List.of(r.getName(), r.getRoomStatus().toString())));
    try {
      report.create(destination);
    } catch (BadAttributeValueExpException e) {
      throw new IOException();
    }
    return destination;
  }

  public String generateUsageReport(List<ReservationWithRoom> reservations) throws IOException {
    LocalDateTime timestamp = LocalDateTime.now();
    String destination = initializeReport(timestamp);
    PdfReport report = createNewReport();
    report
        .landscape()
        .setTitle("Usage Report")
        .setTimestamp(timestamp)
        .setHeaderTable(
            List.of(
                "Reservation ID",
                "User",
                "Desk ID",
                "Room name",
                "Start usage",
                "End usage",
                "Desk cleaned after usage"));
    reservations.stream()
        .filter(r -> r.getUsageStart() != null && r.getUsageEnd() != null)
        .forEach(
            r ->
                report.addRowTable(
                    List.of(
                        r.getId(),
                        r.getUsername(),
                        r.getDeskId(),
                        r.getRoom(),
                        r.getUsageStart().format(DateTimeFormatter.ISO_DATE_TIME),
                        r.getUsageEnd().format(DateTimeFormatter.ISO_DATE_TIME),
                        r.getDeskCleaned().toString())));
    try {
      report.create(destination);
    } catch (BadAttributeValueExpException e) {
      throw new IOException();
    }
    return destination;
  }

  protected PdfReport createNewReport() {
    return new PdfReport();
  }

  public byte[] readReport(String path) throws IOException {
    InputStream inputStream = new FileInputStream(path);
    return inputStream.readAllBytes();
  }

  protected boolean fileExists(String path) {
    return Files.exists(Path.of(path));
  }

  protected void createDirectory(String path) throws IOException {
    Files.createDirectory(Path.of(path));
  }

  protected void createFile(String path) throws IOException {
    Files.createFile(Path.of(path));
  }

  protected String initializeReport(LocalDateTime timestamp) throws IOException {
    String destination =
        pathReport(timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
    if (!fileExists(DESTINATION_DIR + "/")) createDirectory(DESTINATION_DIR);
    createFile(destination);
    return destination;
  }

  protected String pathReport(String id) {
    return DESTINATION_DIR + "/Report_" + id + ".pdf";
  }

  public String setAsVerified(String path) throws IOException {
    Path src = Path.of(path);
    Path dest = Path.of(DESTINATION_DIR + "/Registered_" + src.getFileName());
    Files.move(src, dest, ATOMIC_MOVE);
    return dest.toString();
  }

  public String hashOf(String path) throws IOException {
    return DigestUtils.md5DigestAsHex(readReport(path));
  }
}
