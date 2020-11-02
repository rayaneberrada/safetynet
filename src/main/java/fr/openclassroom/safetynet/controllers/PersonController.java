package fr.openclassroom.safetynet.controllers;

import fr.openclassroom.safetynet.DAO.PersonDAOImpl;
import fr.openclassroom.safetynet.beans.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    private static Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    PersonDAOImpl personDAOimpl;

    @GetMapping(value = "/person")
    public List<Person> getPerson() {
        return personDAOimpl.getPersons();
    }

    @DeleteMapping("/person")
    public List<Person> removePerson(@RequestBody Person person) {
        logger.info(String.valueOf(person));
        return personDAOimpl.deletePerson(person);
    }

    @PostMapping("/person")
    public List<Person> addPerson(@RequestBody Person person) {
        logger.info(String.valueOf(person));
        return personDAOimpl.addPerson(person);
    }

    @PutMapping("/person")
    public List<Person> updatePerson(@RequestBody Person person) {
        logger.info(String.valueOf(person));
        return personDAOimpl.updatePerson(person);
    }
}
