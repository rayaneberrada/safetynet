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

/** Controller exposing routes using stations as parameter or managing crud operations on stations */
@RestController
public class FirestationController {

    private static Logger logger = LoggerFactory.getLogger(FirestationController.class);

    @Autowired
    FirestationDAO firestationDAO;

    @Autowired
    FilterDAO filterDAO;


    /**
     * Route to ask how many person are covered by a specific station.
     * It also list how many of them are kids and how many are adults
     *
     * @param stationNumber
     * @return all the persons, with their firstname, lastname, address and phone number
     */
    @GetMapping(value = "/firestation")
    public Map<String, Object> countAdultAndChildPerStation(int stationNumber) {
        logger.info("http://localhost:8080/firestation?stationNumber=" + stationNumber);
        Map<String, Object> adultAndChildPerStation = null;
        try {
            adultAndChildPerStation = filterDAO.countAdultAndChildPerStation(stationNumber);
            logger.info(String.valueOf(adultAndChildPerStation));
        } catch(Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return adultAndChildPerStation;
    }

    /**
     * Route to ask for the addresses covered by the station(s) defined in @param, the
     * person(s) informations and medical record.
     *
     * @param stations can accept one or more station number ex: ?stations=1&stations=2
     * @return persons with their lastname, age, phone number and medical record
     */
    @GetMapping(value = "/flood/stations")
    public Map<String, List<JsonNode>> personsAndMedicalRecordPerAddressPerStation(String[] stations) {
        String parameters = String.join("&", stations);
        logger.info("http://localhost:8080/flood/stations?" + parameters);
        Map<String, List<JsonNode>> personsAndMedicalRecord = null;
        try {
            personsAndMedicalRecord = filterDAO.getPersonsAndMedicalRecordPerAddressPerStation(stations);
            logger.info(String.valueOf(personsAndMedicalRecord));
        } catch(Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return personsAndMedicalRecord;
    }

    /**
     * Route to ask for the phone numbers informations of the persons living near a station
     *
     * @param stationNumber
     * @return list of phone number
     */
    @GetMapping(value = "/phoneAlert")
    public Map<String, List<String>> getPersonsPhoneForStation(int stationNumber) {
        Map<String, List<String>> phoneNumbers = null;
        logger.info("http://localhost:8080/phoneAlert?stationNumber" + stationNumber);
        try {
            phoneNumbers = filterDAO.getPhoneNumbersForStation(stationNumber);
            logger.info(String.valueOf(phoneNumbers));
        } catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return phoneNumbers;
    }

    /**
     * Route to ask to remove a station from the ones in memory
     *
     * @param stationToDelete
     * @return the stations left
     */
    @DeleteMapping("/firestation")
    public List<Firestation> removeFirestation(@RequestBody Firestation stationToDelete) {
        List<Firestation> stationsAfterDeleting = null;
        logger.info("http://localhost:8080/firestation");
        logger.info("body: " + stationToDelete);
        try {
            stationsAfterDeleting = firestationDAO.deleteFirestation(stationToDelete);
            logger.info(String.valueOf(stationsAfterDeleting));
        } catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return stationsAfterDeleting;
    }

    /**
     * Route to add a firestation to the ones in memory
     *
     * @param stationToAdd
     * @return the station(s) plus the one added
     */
    @PostMapping("/firestation")
    public List<Firestation> addFirestation(@RequestBody Firestation stationToAdd) {
        List<Firestation> stationsAfterAdding = null;
        logger.info("http://localhost:8080/firestation");
        logger.info("body: " + stationToAdd);
        try {
            stationsAfterAdding = firestationDAO.deleteFirestation(stationToAdd);
            logger.info(String.valueOf(stationsAfterAdding));
        } catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return stationsAfterAdding;
    }

    /**
     * Route to modify a firestation to the ones in memory
     *
     * @param stationToUpdate
     * @return the station(s) after modification
     */
    @PutMapping("/firestation")
    public List<Firestation> updateFirestation(@RequestBody Firestation stationToUpdate) {
        List<Firestation> stationsAfterModification = null;
        logger.info("http://localhost:8080/firestation");
        logger.info("body: " + stationToUpdate);
        try {
            stationsAfterModification = firestationDAO.deleteFirestation(stationToUpdate);
            logger.info(String.valueOf(stationsAfterModification));
        } catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return stationsAfterModification;
    }
}