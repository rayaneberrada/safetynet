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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/** Controller exposing routes using person or Person related attributes as parameter or managing crud operations on persons */
@RestController
public class PersonController {

    private static Logger logger = LoggerFactory.getLogger(PersonController.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.serializeAll());

    @Autowired
    PersonDAO personDAOimpl;

    @Autowired
    FilterDAO filterDAO;

    /**
     * Route to ask all persons and persons informations
     *
     * @return all persons
     */
    @GetMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPerson() {
        logger.info("http://localhost:8080/person");
        String persons = null;
        try {
            persons = mapper.writer(personFilter).withDefaultPrettyPrinter().writeValueAsString(personDAOimpl.getPersons());
            logger.info(persons);
        }   catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return persons;
    }

    /**
     * Route to ask emails of persons from a city
     *
     * @param city
     * @return persons emails in city
     */
    @GetMapping(value = "/communityEmail")
    public Map<String, List<String>> personsEmailAtCity(String city) {
        logger.info("http://localhost:8080/communityEmail?city=" + city);
        Map<String, List<String>> personEmails = null;
        try {
            personEmails = filterDAO.getPersonsEmailInCity(city);
            logger.info(String.valueOf(personEmails));
        } catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return personEmails;
    }

    /**
     * Route to ask informations related to persons matching firstName and lastName
     *
     * @param firstName
     * @param lastName
     * @return a list of persons infos
     */
    @GetMapping(value = "/personInfo")
    public List<JsonNode> getPersonInfo(String firstName, String lastName) {
        logger.info("http://localhost:8080/personInfo?firstName=" + firstName + "&lastName=" + lastName);
        List<JsonNode> persons = null;
        try {
            persons = filterDAO.getPersonFiltered(firstName, lastName);
            logger.info(String.valueOf(persons));
        } catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return persons;
    }

    /**
     * Route to ask adults and childs informations for a specific address
     *
     * @param address
     * @return map containing informations for members and childs
     */
    @GetMapping(value = "/childAlert")
    public Map<String, List> getChildsAtAddress(String address) {
        logger.info("http://localhost:8080/childAlert?address=" + address);
        Map<String, List> childs = null;
        try {
            childs = filterDAO.countChildsAtAddress(address);
            logger.info(String.valueOf(childs));
        } catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return childs;
    }

    /**
     * Route to ask people leaving at an address and the station related
     * @param address
     * @return Map listing people informations at address(name, phone, age, medical record) and firestation number
     */
    @GetMapping(value = "/fire")
    public Map<String, Object> getPersonsAndMedicalRecordsAndStationNumberOfAddress(String address) {
        logger.info("http://localhost:8080/fire?address=" + address);
        Map<String, Object> personsAndRecords = null;
        try {
            personsAndRecords = filterDAO.getPersonsMedicalRecordsAndStationNumberOfAddress(address);
            logger.info(String.valueOf(personsAndRecords));
        } catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return personsAndRecords;
    }

    /**
     * Route to delete a person from datas using body informations
     *
     * @param person
     * @return people after deletion
     */
    @DeleteMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
    public String removePerson(@RequestBody Person person) {
        logger.info("http://localhost:8080/person");
        logger.info("body: " + person);
        String personToDelete = null;
        try {
            personToDelete = mapper.writer(personFilter).withDefaultPrettyPrinter().writeValueAsString(personDAOimpl.deletePerson(person));
            logger.info(personToDelete);
        }   catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return personToDelete;
    }

    /**
     * Route to add a person to datas using body informations
     *
     * @param person
     * @return people after adding one
     */
    @PostMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addPerson(@RequestBody Person person) {
        logger.info("http://localhost:8080/person");
        logger.info("body: " + person);
        String personToAdd = null;
        try {
            personToAdd = mapper.writer(personFilter).withDefaultPrettyPrinter().writeValueAsString(personDAOimpl.addPerson(person));
            logger.info(personToAdd);
        }   catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return personToAdd;
    }

    /**
     * Route to modify a person from datas using body informations
     *
     * @param person
     * @return people after modification
     */
    @PutMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updatePerson(@RequestBody Person person) {
        logger.info("http://localhost:8080/person");
        logger.info("body: " + person);
        String personToModify = null;
        try {
            personToModify = mapper.writer(personFilter).withDefaultPrettyPrinter().writeValueAsString(personDAOimpl.addPerson(person));
            logger.info(personToModify);
        }   catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return personToModify;
    }
}
