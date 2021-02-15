package it.sweven.blockcovid.entities;

/* Java utilities */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/* Spring utilities */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/* Custom packages */
import it.sweven.blockcovid.entities.User;

@Document
public class Reservation {
    private @Id String id;
    private String nameRoom;
    private Integer idDesk;
    private LocalDateTime from, to;
    private @CreatedBy final String user;  // reference User

    @Transient
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Transient
    private final ChronoUnit granularity = ChronoUnit.MINUTES;

    @PersistenceConstructor
    public Reservation(String id, String nameRoom,
                       Integer idDesk, LocalDateTime from,
                       LocalDateTime to, String user) {
        this.id = id;
        this.nameRoom = nameRoom;
        this.idDesk = idDesk;
        this.from = from.truncatedTo(granularity);
        this.to = to.truncatedTo(granularity);
        this.user = user;
    }

    public Reservation(String nameRoom,
                       Integer idDesk, String from,
                       String to, String user) {
        this.nameRoom = nameRoom;
        this.idDesk = idDesk;
        this.from = LocalDateTime.parse(from, formatter).truncatedTo(granularity);
        this.to = LocalDateTime.parse(to, formatter).truncatedTo(granularity);
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public Integer getIdDesk() {
        return idDesk;
    }

    public void setIdDesk(Integer idDesk) {
        this.idDesk = idDesk;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from.truncatedTo(ChronoUnit.MINUTES);
    }

    public void setFrom(String from) {
        this.from = LocalDateTime.parse(from, formatter).truncatedTo(granularity);
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to.truncatedTo(granularity);
    }

    public void setTo(String to) {
        this.to = LocalDateTime.parse(to, formatter).truncatedTo(granularity);
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", nameRoom=" + nameRoom  +
                ", idDesk=" + idDesk +
                ", from=" + from +
                ", to=" + to +
                ", user=" + user +
                '}';
    }

    public boolean conflicts(Reservation other) {
        if(this.nameRoom.equals(other.nameRoom) && this.idDesk.equals(other.idDesk)) {
            return (!this.from.isAfter(other.from) && this.to.isAfter(other.from)) ||
                    (!other.from.isAfter(this.from) && other.to.isAfter(this.from));
        }
        return false;
    }
}
