package it.sweven.blockcovid.entities;

/* Java utilities */
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/* Spring utilities */
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Reservation {
    private @Id String id;
    private String nameRoom;
    private Integer idDesk;
    private LocalDate date;
    private LocalTime from, to;
    private @CreatedBy final String user;  // reference User

    @Transient
    private static final ChronoUnit granularity = ChronoUnit.MINUTES;

    @PersistenceConstructor
    public Reservation(String id, String nameRoom,
                       Integer idDesk, LocalDate date,
                       LocalTime from, LocalTime to,
                       String user) {
        this.id = id;
        this.nameRoom = nameRoom;
        this.idDesk = idDesk;
        this.date = date;
        this.from = from.truncatedTo(granularity);
        this.to = to.truncatedTo(granularity);
        this.user = user;
    }

    public Reservation(String nameRoom, Integer idDesk,
                       LocalDate date, LocalTime from,
                       LocalTime to, String user) {
        this.nameRoom = nameRoom;
        this.idDesk = idDesk;
        this.date = date;
        if(from.isAfter(to))
            throw new IllegalArgumentException("'from' should come before 'to'");
        this.from = from.truncatedTo(granularity);
        this.to = to.truncatedTo(granularity);
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        if(from.isAfter(to))
            throw new IllegalArgumentException("'from' should come before 'to'");
        this.from = from.truncatedTo(ChronoUnit.MINUTES);
    }

    public LocalTime getTo() {
        return to;
    }

    public void setTo(LocalTime to) {
        if(from.isAfter(to))
            throw new IllegalArgumentException("'to' should come after 'from'");
        this.to = to.truncatedTo(granularity);
    }

    public void setTime(LocalTime from, LocalTime to) throws IllegalArgumentException {
        if(from.isAfter(to))
            throw new IllegalArgumentException("'from' should come before 'to'");
        this.from = from;
        this.to = to;
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
                ", date=" + date +
                ", from=" + from +
                ", to=" + to +
                ", user=" + user +
                '}';
    }

    public boolean conflicts(Reservation other) {
        if(this.nameRoom.equals(other.nameRoom) && this.idDesk.equals(other.idDesk) &&
            this.date.isEqual(other.date)) {
            return (!this.from.isAfter(other.from) && this.to.isAfter(other.from)) ||
                    (!other.from.isAfter(this.from) && other.to.isAfter(this.from));
        }
        return false;
    }
}
