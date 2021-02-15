package it.sweven.blockcovid.entities;

/* Java utilities */
import java.util.Timestamp;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/* Spring utilities */
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Token{
    private byte[] token;
    
    /* Gli si può dare una stringa qualsiasi. MessageDigest non è
     * thread-safe, ma per i costruttori dovrei aver letto che non ci
     * sono problemi dato che la JVM non consegna ad altri thread
     * degli oggetti non costruiti completamente */
    public Token(String entropy){
	MessageDigest digester = MessageDigest.getInstance("SHA-256");
	token = digester.digest(entropy.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
	return new String(token, StandardCharsets.UTF_8);
    }

    @Override
    public boolean equals(Object o) {
	if(o instanceof Token) {
	    Token other = (Token)o;
	    return this.toString().equals(other.toString());
	} else return false;
    }
}
