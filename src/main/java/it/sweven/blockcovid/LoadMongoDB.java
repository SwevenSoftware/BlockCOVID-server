package it.sweven.blockcovid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EntitaRepo repository) {
	return args -> {
	    log.info("Preloading " + repository.save(new Entita("marco", "aaa", "bbb", new Integer(2), 3)));
	    log.info("Preloading " + repository.save(new Entita("giada", "aaa", "bbb", new Integer(2), 4)));
	};
    }
}
