package it.sweven.blockcovid.blockchain.controllers;

import ch.qos.logback.core.read.ListAppender;
import it.sweven.blockcovid.blockchain.services.BlockchainService;
import it.sweven.blockcovid.blockchain.services.DocumentContractService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.rooms.services.RoomService;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.LoggingEvent;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BlockchainUsageControllerTest {
    private BlockchainService blockchainService;
    private ReservationService reservationService;
    private DocumentService documentService;
    private BlockchainUsageController controller;
    ListAppender<LoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        blockchainService = mock(BlockchainService.class);
        documentService = mock(DocumentService.class);
        reservationService = mock(ReservationService.class);
        DocumentContractService documentContractService = mock(DocumentContractService.class);
        Credentials accountCredentials = mock(Credentials.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        controller =
                new BlockchainUsageController(
                        blockchainService,
                        documentContractService,
                        documentService,
                        reservationService,
                        accountCredentials);
        Logger logger = LoggerFactory.getLogger(BlockchainUsageController.class);
        LogManager.getLogger("BlockchainController");
    }

    @Test
    void runWithNoProblemsShouldNotThrowException() throws Exception {
        TransactionReceipt fakeReceipt = mock(TransactionReceipt.class);
        List<ReservationWithRoom> listReservations = List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class));
        when(reservationService.findByTimeInterval(any(), any())).thenReturn(listReservations);
        when(documentService.generateUsageReport(listReservations)).thenReturn("path");
        when(blockchainService.registerReport(any(), any())).thenReturn(fakeReceipt);
        Files.createFile(Path.of("path"));
        assertDoesNotThrow(controller::run);
        Files.delete(Path.of("path"));
    }

    @Test
    void generationOfCleanerReportThrowsException_throwsIoException() throws IOException {
        when(documentService.generateUsageReport(any())).thenThrow(new IOException());
        assertThrows(IOException.class, controller::run);
    }

    @Test
    void invalidSavedPath_throwsIoException() throws IOException {
        when(documentService.generateUsageReport(any())).thenReturn("InvalidPath");
        assertThrows(IOException.class, controller::run);
    }

    @Test
    void registerReportFails_throwsException() throws Exception {
        when(documentService.generateUsageReport(any())).thenReturn("path");
        when(blockchainService.registerReport(any(), any())).thenThrow(new Exception());
        Files.createFile(Path.of("path"));
        assertThrows(Exception.class, controller::run);
        Files.delete(Path.of("path"));
    }
}