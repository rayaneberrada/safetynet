package fr.openclassroom.safetynet.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.openclassroom.safetynet.DAO.FirestationDAO;
import fr.openclassroom.safetynet.beans.Firestation;
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
public class FirestationController {

    private static Logger logger = LoggerFactory.getLogger(FirestationController.class);

    @Autowired
    FirestationDAO firestationDAO;

    @Autowired
    FilterDAO filterDAO;


    @GetMapping(value = "/firestation")
    public Map<String, Object> getFirestation(int stationNumber) throws java.text.ParseException, IOException {
        return filterDAO.countAdultAndChildPerStation(stationNumber);
    }

    @RequestMapping(value = "/flood/stations")
    public Map<String, List<JsonNode>> PersonsAndMedicalRecordPerAddressPerStation(String[] stations) throws java.text.ParseException, JsonProcessingException {
        return filterDAO.getPersonsAndMedicalRecordPerAddressPerStation(stations);
    }


    @RequestMapping(value = "/phoneAlert")
    public Map<String, List<String>> getPersonsPhoneForStation(int firestation) throws java.text.ParseException, JsonProcessingException {
        return filterDAO.getPhoneNumbersForStation(firestation);
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