package it.sweven.blockcovid.documents;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PdfReportTest {

  private PdfReport report;

  @BeforeEach
  void setUp() {
    report = mock(PdfReport.class);
  }

  @Test
  void save() throws IOException {
    Document mockDocument = mock(Document.class);
    doReturn(mockDocument).when(report).initializeDocument(any());
    doNothing().when(report).drawHeader();
    AtomicBoolean documentClosed = new AtomicBoolean(false);
    doAnswer(
            invocation -> {
              documentClosed.set(true);
              return null;
            })
        .when(report)
        .save();
    when(report.save()).thenCallRealMethod();
    report.save();
    assertTrue(documentClosed.get());
  }

  @Test
  void setTableHeader() {
    List<String> providedList = List.of("Header1", "Header2", "Header3");
    Table mockTable = mock(Table.class);
    doReturn(mockTable).when(report).createNewTable(providedList.size());
    AtomicInteger cellsAdded = new AtomicInteger(0);
    doAnswer(
            invocation -> {
              cellsAdded.getAndAdd(1);
              return null;
            })
        .when(mockTable)
        .addCell(any(Cell.class));
    Cell mockCell = mock(Cell.class);
    doReturn(mockCell).when(report).createNewCell();
    when(mockCell.setBold()).thenReturn(mockCell);
    when(mockCell.add(any(Paragraph.class))).thenReturn(mockCell);
    doAnswer(
            invocation -> {
              assertTrue(providedList.contains(invocation.getArgument(0, String.class)));
              return mock(Paragraph.class);
            })
        .when(report)
        .createNewParagraph(any());
    doCallRealMethod().when(report).setTableHeader(anyList());
    report.setTableHeader(providedList);
    assertEquals(providedList.size(), cellsAdded.get());
  }

  @Test
  void addRowTable() {
    List<String> providedList = List.of("Paragraph1", "Paragraph2", "Paragraph3", "Paragraph4");
    Table mockTable = mock(Table.class);
    doReturn(mockTable).when(report).createNewTable(providedList.size());
    when(mockTable.getNumberOfColumns()).thenReturn(providedList.size());
    AtomicInteger cellsAdded = new AtomicInteger(0);
    doAnswer(
            invocation -> {
              cellsAdded.getAndAdd(1);
              return null;
            })
        .when(mockTable)
        .addCell(any(Cell.class));
    Cell mockCell = mock(Cell.class);
    doReturn(mockCell).when(report).createNewCell();
    when(mockCell.add(any(Paragraph.class))).thenReturn(mockCell);
    doAnswer(
            invocation -> {
              assertTrue(providedList.contains(invocation.getArgument(0, String.class)));
              return mock(Paragraph.class);
            })
        .when(report)
        .createNewParagraph(any());
    doCallRealMethod().when(report).addRowTable(anyList());
    report.addRowTable(providedList);
    assertEquals(providedList.size(), cellsAdded.get());
  }

  @Test
  void initializeDocument() throws IOException {
    doReturn(false).when(report).pathExists(any());
    AtomicBoolean dirCreated = new AtomicBoolean(false);
    doAnswer(
            invocation -> {
              dirCreated.set(true);
              return null;
            })
        .when(report)
        .createDirectory(any());
    String expectedPath = PdfReport.pathFile("path");
    AtomicBoolean newFileCreated = new AtomicBoolean(false);
    doAnswer(
            invocation -> {
              newFileCreated.set(true);
              return null;
            })
        .when(report)
        .createNewFile(expectedPath);
    Document mockDocument = mock(Document.class);
    when(report.createNewDocument(expectedPath)).thenReturn(mockDocument);
    doCallRealMethod().when(report).initializeDocument(any());
    assertEquals(mockDocument, report.initializeDocument("path"));
    assertTrue(newFileCreated.get());
    assertTrue(dirCreated.get());
  }
}
