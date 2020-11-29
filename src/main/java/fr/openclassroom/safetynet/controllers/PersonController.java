package fr.openclassroom.safetynet.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import fr.openclassroom.safetynet.DAO.PersonDAO;
import fr.openclassroom.safetynet.beans.Person;
import fr.openclassroom.safetynet.DAO.FilterDAO;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class PersonController {

    private static Logger logger = LoggerFactory.getLogger(PersonController.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.serializeAll());

    @Autowired
    PersonDAO personDAOimpl;

    @Autowired
    FilterDAO filterDAO;

    @GetMapping(value = "/person")
    public String Person() throws JsonProcessingException {
        return mapper.writer(personFilter).withDefaultPrettyPrinter().writeValueAsString(personDAOimpl.getPersons());
    }

    @RequestMapping(value = "/communityEmail")
    public Map<String, List<String>> personsEmailAtCity(String city) throws IOException {
        return filterDAO.getPersonsEmailInCity(city);
    }

    @RequestMapping(value = "/personInfo")
    public List<JsonNode> personInfo(String firstName, String lastName) throws IOException, java.text.ParseException {
        return filterDAO.getPersonFiltered(firstName, lastName);
    }

    @RequestMapping(value = "/childAlert")
    public Map<String, List> childsAtAddress(String address) throws java.text.ParseException, IOException {
        return filterDAO.countChildsAtAddress(address);
    }

    @RequestMapping(value = "/fire")
    public Map<String, Object> PersonsAndMedicalRecordsAndStationNumberOfAddress(String address) throws JsonProcessingException {
        return filterDAO.getPersonsMedicalRecordsAndStationNumberOfAddress(address);
    }

    @DeleteMapping("/person")
    public String removePerson(@RequestBody Person person) throws JsonProcessingException {
        logger.info(String.valueOf(person));
        return mapper.writer(personFilter).withDefaultPrettyPrinter().writeValueAsString(personDAOimpl.deletePerson(person));
    }

    @PostMapping("/person")
    public String addPerson(@RequestBody Person person) throws IOException, ParseException {
        logger.info(String.valueOf(person));
        return mapper.writer(personFilter).withDefaultPrettyPrinter().writeValueAsString(personDAOimpl.addPerson(person));
    }

    @PutMapping("/person")
    public String updatePerson(@RequestBody Person person) throws IOException, ParseException {
        logger.info(String.valueOf(person));
        return mapper.writer(personFilter).withDefaultPrettyPrinter().writeValueAsString(personDAOimpl.updatePerson(person));
    }
}
