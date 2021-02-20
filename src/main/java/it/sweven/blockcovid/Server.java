package it.sweven.blockcovid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
// @PropertySource("classpath:application.properties")
public class Server {
    
    public static void main(String[] args) {
	SpringApplication secureServer =  new SpringApplication(Server.class);
	// secureServer.setAdditionalProfiles("ssl");
	secureServer.run(args);
    }

}
