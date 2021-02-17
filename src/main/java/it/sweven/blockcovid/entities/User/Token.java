package it.sweven.blockcovid.entities.User;

/* Java utilities */
import java.sql.Timestamp;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/* Spring utilities */
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Token{
    /* Gli si può dare una stringa qualsiasi. MessageDigest non è
     * thread-safe, ma per i costruttori dovrei aver letto che non ci
     * sono problemi dato che la JVM non consegna ad altri thread
     * degli oggetti non costruiti completamente */
    public static String generateToken(String entropy){
	try{
	    MessageDigest digester = MessageDigest.getInstance("SHA-256");
	    return new String(digester.digest(entropy.getBytes(StandardCharsets.UTF_8)));
	} catch (Exception NoAlgorithm) {
	    System.err.println("Rip");
	}
	return null;
    }
}
