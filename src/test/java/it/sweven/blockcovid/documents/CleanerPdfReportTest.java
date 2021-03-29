package it.sweven.blockcovid.documents;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.room.Status;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CleanerPdfReportTest {

  private CleanerPdfReport cleanerReport;

  @BeforeEach
  void setUp() {
    cleanerReport = spy(new CleanerPdfReport());
  }

  @Test
  void addRoom() {
    Room expectedRoom = mock(Room.class);
    cleanerReport.addRoom(expectedRoom);
    assertTrue(cleanerReport.getRooms().contains(expectedRoom));
  }

  @Test
  void save() throws IOException {
    Room mockRoom1 = mock(Room.class), mockRoom2 = mock(Room.class);
    when(mockRoom1.getName()).thenReturn("room1");
    when(mockRoom1.getRoomStatus()).thenReturn(Status.CLEAN);
    when(mockRoom2.getName()).thenReturn("room2");
    when(mockRoom2.getRoomStatus()).thenReturn(Status.DIRTY);
    cleanerReport.addRoom(mockRoom1);
    cleanerReport.addRoom(mockRoom2);

    PdfReport pdfReport = mock(PdfReport.class);
    when(cleanerReport.createNewPdfReport()).thenReturn(pdfReport);

    AtomicInteger lengthHeader = new AtomicInteger(0);
    doAnswer(
            invocation -> {
              lengthHeader.set(invocation.getArgument(0, List.class).size());
              return null;
            })
        .when(pdfReport)
        .setTableHeader(anyList());
    doAnswer(
            invocation -> {
              assertEquals(lengthHeader.get(), invocation.getArgument(0, List.class).size());
              return null;
            })
        .when(pdfReport)
        .addRowTable(anyList());

    AtomicBoolean reportSaved = new AtomicBoolean(false);
    when(pdfReport.save())
        .thenAnswer(
            invocation -> {
              reportSaved.set(true);
              return "path";
            });

    assertEquals("path", cleanerReport.save());
    assertTrue(reportSaved.get());
  }
}
