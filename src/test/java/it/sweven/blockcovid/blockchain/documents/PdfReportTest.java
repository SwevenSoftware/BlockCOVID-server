package it.sweven.blockcovid.blockchain.documents;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PdfReportTest {

  private PdfReport report;

  @BeforeEach
  void setUp() {
    report = spy(new PdfReport());
  }

  @Test
  void setTableHeader() {
    List<String> providedList = List.of("Header1", "Header2");
    Cell mockCell1 = mock(Cell.class);
    doReturn(mockCell1).when(mockCell1).add(any(IBlockElement.class));
    doReturn(mockCell1).when(report).createNewCell();
    List<Cell> expectedList = List.of(mockCell1, mockCell1);
    Paragraph mockParagraph = mock(Paragraph.class);
    when(mockParagraph.setBold()).thenReturn(mockParagraph);
    doReturn(mockParagraph).when(report).createNewParagraph(any());
    assertEquals(expectedList, report.setHeaderTable(providedList).getTableHeader());
  }

  @Test
  void addRowTable() {
    List<String> providedList = List.of("Row1", "Row2");
    Cell mockCell1 = mock(Cell.class);
    doReturn(mockCell1).when(mockCell1).add(any(IBlockElement.class));
    doReturn(mockCell1).when(report).createNewCell();
    List<Cell> expectedList = List.of(mockCell1, mockCell1);
    Paragraph mockParagraph = mock(Paragraph.class);
    when(mockParagraph.setBold()).thenReturn(mockParagraph);
    doReturn(mockParagraph).when(report).createNewParagraph(any());
    assertEquals(expectedList, report.addRowTable(providedList).getRowsTable().get(0));
  }

  @Test
  void addTitle() {
    report.setTitle("Title");
    doAnswer(
            invocation -> {
              assertTrue(invocation.getArgument(0, String.class).contains("Title"));
              return mock(Paragraph.class);
            })
        .when(report)
        .createNewParagraph(any());
    report.addTitle(mock(Document.class));
  }

  @Test
  void addTimestamp() {
    report.setTimestamp(LocalDateTime.of(2021, 4, 10, 20, 10, 50));
    doAnswer(
            invocation -> {
              assertTrue(invocation.getArgument(0, String.class).contains("2021-04-10 20:10:50"));
              return mock(Paragraph.class);
            })
        .when(report)
        .createNewParagraph(any());
    report.addTimestamp(mock(Document.class));
  }

  @Test
  void addTable_invalidHeader_throwsBadAttributeValueExpException() {
    assertThrows(BadAttributeValueExpException.class, () -> report.addTable(mock(Document.class)));
    report.setHeaderTable(Collections.emptyList());
    assertThrows(BadAttributeValueExpException.class, () -> report.addTable(mock(Document.class)));
  }

  @Test
  void addTable_validHeader() throws BadAttributeValueExpException {
    List<String> providedHeader = List.of("Header1", "Header2");
    Table mockTable = mock(Table.class);
    report.setHeaderTable(providedHeader);
    doAnswer(
            invocation -> {
              assertEquals(providedHeader.size(), invocation.getArgument(0, Integer.class));
              return mockTable;
            })
        .when(report)
        .createNewTable(any(Integer.class));
    when(mockTable.getNumberOfColumns()).thenReturn(providedHeader.size());
    Document mockDocument = mock(Document.class);
    AtomicBoolean tableAdded = new AtomicBoolean(false);
    when(mockDocument.add(mockTable))
        .thenAnswer(
            invocation -> {
              tableAdded.set(true);
              return null;
            });
    report.addTable(mockDocument);
    assertTrue(tableAdded.get());
  }

  @Test
  void create() throws FileNotFoundException, BadAttributeValueExpException {
    Document mockDocument = mock(Document.class);
    doReturn(mockDocument).when(report).createNewDocument("path");
    PdfDocument mockPdfDocument = mock(PdfDocument.class);
    when(mockDocument.getPdfDocument()).thenReturn(mockPdfDocument);
    doNothing().when(report).addTitle(any());
    doNothing().when(report).addTimestamp(any());
    doNothing().when(report).addTable(any());
    report.landscape().create("path");
    verify(mockDocument).close();
    verify(mockPdfDocument).setDefaultPageSize(any());
  }
}
