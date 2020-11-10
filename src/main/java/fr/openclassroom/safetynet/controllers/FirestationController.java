package fr.openclassroom.safetynet.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.openclassroom.safetynet.DAO.FirestationDAO;
import fr.openclassroom.safetynet.beans.Firestation;
import fr.openclassroom.safetynet.beans.Person;
import fr.openclassroom.safetynet.services.DataFilter;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FirestationController {

    private static Logger logger = LoggerFactory.getLogger(FirestationController.class);

    @Autowired
    FirestationDAO firestationDAO;

    @Autowired
    DataFilter dataFilter;

    @GetMapping(value = "/firestation")
    public List<Firestation> getFirestation() {
        return firestationDAO.getFirestations();
    }

    @RequestMapping(value = "/firestation/{stationNumber}")
    public Map<String, Object> AdultAndChildPerStation(@PathVariable int stationNumber) throws java.text.ParseException {
        return dataFilter.countAdultAndChildPerStation(stationNumber);
    }

    @RequestMapping(value = "/flood/stations/{stations}")
    public Map<String, List<Object>> PersonsAndMedicalRecordPerAddressPerStation(@PathVariable List<Integer> stationsNumbers) throws java.text.ParseException {
        System.out.println("Il ya qqun?");
        logger.info("Les stations requêtées sont: " + stationsNumbers);
        return dataFilter.getPersonsAndMedicalRecordPerAddressPerStation(stationsNumbers);
    }


    @RequestMapping(value = "/phoneAlert/{stationNumber}")
    public List<Person> getPersonsPhoneForStation(@PathVariable int stationNumber) throws java.text.ParseException {
        return dataFilter.getPhoneNumbersForStation(stationNumber);
    }

    @DeleteMapping("/firestation")
    public List<Firestation> removeFirestation(@RequestBody Firestation stationToDelete) {
        logger.info(String.valueOf(stationToDelete));
        return firestationDAO.deleteFirestation(stationToDelete);
    }

    @PostMapping("/firestation")
    public List<Firestation> addFirestation(@RequestBody Firestation stationToAdd) throws IOException, ParseException {
        logger.info(String.valueOf(stationToAdd));
        return firestationDAO.addFirestation(stationToAdd);
    }

    @PutMapping("/firestation")
    public List<Firestation> updateFirestation(@RequestBody Firestation stationToUpdate) throws IOException, ParseException {
        logger.info(String.valueOf(stationToUpdate));
        return firestationDAO.updateFirestation(stationToUpdate);
    }
}