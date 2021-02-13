package it.sweven.blockcovid;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class MongoController {
    @Autowired
    private final EntitaRepo repository;

    MongoController(EntitaRepo repo) {
	this.repository = repo;
    }

    @GetMapping("/")
    String hello() {
	return "Hello, to view Entities in this db send GET requests at /p";
    }

    @GetMapping("/p")
    String mongoMain() {
        return repository.findAll().toString();
    }

    @GetMapping("/p/{UUID}")
    Entita mongoUUID(@PathVariable String UUID) {
	return repository.findByUUID(UUID);
    }
}
