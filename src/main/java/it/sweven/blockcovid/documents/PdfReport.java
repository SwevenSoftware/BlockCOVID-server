package it.sweven.blockcovid.documents;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class PdfReport {
  private Document document;
  private LocalDateTime timestamp;
  private String destination;
  private Table table;

  protected boolean pathExists(String path) {
    return Files.exists(Path.of(path));
  }

  protected void createDirectory(String path) throws IOException {
    Files.createDirectory(Path.of(path));
  }

  protected void createNewFile(String path) throws IOException {
    Files.createFile(Path.of(path));
  }

  protected Document createNewDocument(String path) throws FileNotFoundException {
    return new Document(new PdfDocument(new PdfWriter(path)));
  }

  protected Paragraph createNewParagraph(String text) {
    return new Paragraph(text);
  }

  protected Cell createNewCell() {
    return new Cell();
  }

  protected Table createNewTable(int columns) {
    return new Table(columns);
  }

  protected void drawHeader() {
    document.add(createNewParagraph("BlockCOVID Report - Rooms cleaned").setBold());
    document.add(
        createNewParagraph(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
  }

  protected Document initializeDocument(String id) throws IOException {
    if (!pathExists("reports/")) createDirectory("reports");
    destination = pathFile(id);
    createNewFile(destination);
    return createNewDocument(destination);
  }

  String save() throws IOException {
    timestamp = LocalDateTime.now();
    document = initializeDocument(timestamp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    drawHeader();
    if (table != null) document.add(table);
    document.close();
    return destination;
  }

  void setTableHeader(List<String> header) {
    table = createNewTable(header.size());
    header.forEach(h -> table.addCell(createNewCell().setBold().add(createNewParagraph(h))));
  }

  void addRowTable(List<String> paragraphs) {
    if (table == null) table = createNewTable(paragraphs.size());
    paragraphs.stream()
        .limit(table.getNumberOfColumns())
        .forEachOrdered(p -> table.addCell(createNewCell().add(createNewParagraph(p))));
  }

  protected static String pathFile(String id) {
    return "reports/Report_" + id + ".pdf";
  }
}
