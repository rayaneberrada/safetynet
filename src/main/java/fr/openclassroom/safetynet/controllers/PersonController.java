package fr.openclassroom.safetynet.controllers;

import fr.openclassroom.safetynet.DAO.PersonDAO;
import fr.openclassroom.safetynet.DAO.PersonDAOImpl;
import fr.openclassroom.safetynet.beans.Person;
import fr.openclassroom.safetynet.services.DataFilter;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class PersonController {

    private static Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    PersonDAO personDAOimpl;

    @Autowired
    DataFilter dataFilter;

    @GetMapping(value = "/person")
    public List<Person> Person() {
        return personDAOimpl.getPersons();
    }

    @RequestMapping(value = "/childAlert/{address}")
    public Map<String, List> childsAtAddress(@PathVariable String address) throws java.text.ParseException {
        return dataFilter.countChildsAtAddress(address);
    }

    @RequestMapping(value = "/fire/{address}")
    public List<List<Object>> PersonsAndMedicalRecordsAndStationNumberOfAddress(@PathVariable String address){
        return dataFilter.getPersonsMedicalRecordsAndStationNumberOfAddress(address);
    }

    @GetMapping(value = "/communityEmail")
    public List<Person> getEmails() {
        return null;
    }

    @DeleteMapping("/person")
    public List<Person> removePerson(@RequestBody Person person) {
        logger.info(String.valueOf(person));
        return personDAOimpl.deletePerson(person);
    }

    @PostMapping("/person")
    public List<Person> addPerson(@RequestBody Person person) throws IOException, ParseException {
        logger.info(String.valueOf(person));
        return personDAOimpl.addPerson(person);
    }

    @PutMapping("/person")
    public List<Person> updatePerson(@RequestBody Person person) throws IOException, ParseException {
        logger.info(String.valueOf(person));
        return personDAOimpl.updatePerson(person);
    }
}
