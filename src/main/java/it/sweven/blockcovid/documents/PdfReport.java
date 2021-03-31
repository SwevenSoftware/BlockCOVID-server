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

  private final @Getter(AccessLevel.PROTECTED) Document document;
  private String title;
  private LocalDateTime timestamp;
  private List<Cell> tableHeader;
  private final ArrayList<List<Cell>> rowsTable;

  public PdfReport(String path) throws FileNotFoundException {
    document = new Document(new PdfDocument(new PdfWriter(path)));
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

  public PdfReport setHeaderTable(List<String> header) {
    tableHeader =
        header.stream().map(h -> new Cell().add(new Paragraph(h))).collect(Collectors.toList());
    return this;
  }

  public PdfReport addRowTable(List<String> row) {
    rowsTable.add(
        row.stream().map(r -> new Cell().add(new Paragraph(r))).collect(Collectors.toList()));
    return this;
  }

  public void create() throws BadAttributeValueExpException {
    if (tableHeader == null || tableHeader.isEmpty() || title == null || title.isBlank())
      throw new BadAttributeValueExpException(null);
    getDocument().add(new Paragraph("BlockCOVID - " + title).setBold());
    getDocument()
        .add(new Paragraph(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    Table table = new Table(tableHeader.size());
    rowsTable.forEach(
        r -> r.stream().limit(table.getNumberOfColumns()).forEachOrdered(table::addCell));
    getDocument().add(table);
    getDocument().close();
  }
}
