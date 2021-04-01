package it.sweven.blockcovid.documents;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.management.BadAttributeValueExpException;
import lombok.AccessLevel;
import lombok.Getter;

public class PdfReport {
  private String title;
  private LocalDateTime timestamp;
  private @Getter(AccessLevel.PROTECTED) List<Cell> tableHeader;
  private @Getter(AccessLevel.PROTECTED) final ArrayList<List<Cell>> rowsTable;

  public PdfReport() {
    rowsTable = new ArrayList<>();
  }

  public PdfReport setTitle(String title) {
    this.title = title;
    return this;
  }

  public PdfReport setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  protected Cell createNewCell() {
    return new Cell();
  }

  protected Paragraph createNewParagraph(String text) {
    return new Paragraph(text);
  }

  protected Table createNewTable(int columns) {
    return new Table(columns);
  }

  public PdfReport setHeaderTable(List<String> header) {
    tableHeader =
        header.stream()
            .map(h -> createNewCell().add(createNewParagraph(h).setBold()))
            .collect(Collectors.toList());
    return this;
  }

  public PdfReport addRowTable(List<String> row) {
    rowsTable.add(
        row.stream()
            .map(r -> createNewCell().add(createNewParagraph(r)))
            .collect(Collectors.toList()));
    return this;
  }

  protected Document createNewDocument(String path) throws FileNotFoundException {
    return new Document(new PdfDocument(new PdfWriter(path)));
  }

  protected void addTitle(Document document) {
    if (title != null && !title.isBlank())
      document.add(createNewParagraph("BlockCOVID - " + title).setBold());
  }

  protected void addTimestamp(Document document) {
    if (timestamp != null)
      document.add(
          createNewParagraph(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
  }

  protected void addTable(Document document) throws BadAttributeValueExpException {
    if (tableHeader == null || tableHeader.isEmpty()) throw new BadAttributeValueExpException(null);
    Table table = createNewTable(tableHeader.size());
    tableHeader.forEach(table::addCell);
    rowsTable.forEach(
        r -> r.stream().limit(table.getNumberOfColumns()).forEachOrdered(table::addCell));
    document.add(table);
  }

  public void create(String path) throws BadAttributeValueExpException, FileNotFoundException {
    Document document = createNewDocument(path);
    addTitle(document);
    addTimestamp(document);
    addTable(document);
    document.close();
  }
}
