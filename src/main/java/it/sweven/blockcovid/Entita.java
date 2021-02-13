package it.sweven.blockcovid;
import org.springframework.data.annotation.Id;

public class Entita {
    @Id
    public String UUID;
    public String val1, val2;
    public Integer val3; // wrapper object
    public int val4; // primitive value

    public Entita() {}

    public Entita(String uuid, String val1, String val2, Integer val3, int val4){
	this.UUID = uuid;
	this.val1 = val1;
	this.val2 = val2;
	this.val3 = val3;
	this.val4 = val4;
    }

    @Override
    public String toString() {
	return String.format("{ uuid='%s', val1='%s', val2='%s', val3=%s, val4=%d }",
			     UUID, val1, val2, val3.toString(), val4);
    }
}
