package it.sweven.blockcovid.documents;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import it.sweven.blockcovid.entities.Reservation;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.util.DigestUtils;

public class PdfReport {
  private final Document document;
  private final String destination;
  private final LocalDate date;
  private final LocalTime time;
  private List<Reservation> reservations;
  private String hashPreviousReport;

  public PdfReport(LocalDate date, LocalTime time) throws IOException {
    this.date = date;
    this.time = time;
    destination = pathFile(date, time);
    if (!Files.exists(Path.of("reports/"))) Files.createDirectory(Path.of("reports"));
    Files.createFile(Path.of(destination));
    PdfDocument pdf = new PdfDocument(new PdfWriter(destination));
    document = new Document(pdf);
  }

  public PdfReport addReservations(List<Reservation> reservations) {
    this.reservations = reservations;
    return this;
  }

  public PdfReport addHashPreviousReport(String hash) {
    this.hashPreviousReport = hash;
    return this;
  }

  public PdfReport save() {
    String datetime =
        date.format(DateTimeFormatter.ISO_DATE)
            + " "
            + time.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_TIME);
    document.add(new Paragraph(datetime));
    reservations.forEach(r -> document.add(new Paragraph(r.toString())));
    document.add(new Paragraph("MD5 hash previous report: " + hashPreviousReport));
    document.close();
    return this;
  }

  public String filename() {
    return destination;
  }

  public String hashFile() {
    try {
      return DigestUtils.md5DigestAsHex(new FileInputStream(destination));
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static String pathFile(LocalDate date, LocalTime time) {
    return "reports/Report_"
        + date.format(DateTimeFormatter.BASIC_ISO_DATE)
        + "_"
        + time.format(DateTimeFormatter.ofPattern("HHmmss"))
        + ".pdf";
  }
}
