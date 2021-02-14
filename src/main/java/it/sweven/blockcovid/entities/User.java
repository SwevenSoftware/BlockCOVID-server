package it.sweven.blockcovid.entities;

/* Java utilities */
import java.util.Set;

/* Spring utilities */
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {

    private @Id String name;
    private String password;
    // private Token token;
    // private Set<Authorization> authorizations;

    public User(String name, String password) {
	this.name = name;
	this.password = password;
    }

    // public boolean isUser() { return false; }
    
    // public boolean isAdmin() { return false; }

    public String getName() { return name; }
    
    @Override
    public String toString() {
	return String.format("{'name': '%s', 'password': '%s'}",
			     name, password);
    }
}
