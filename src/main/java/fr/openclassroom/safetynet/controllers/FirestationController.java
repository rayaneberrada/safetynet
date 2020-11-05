package fr.openclassroom.safetynet.controllers;

import fr.openclassroom.safetynet.DAO.FirestationDAO;
import fr.openclassroom.safetynet.beans.Firestation;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class FirestationController {

    private static Logger logger = LoggerFactory.getLogger(FirestationController.class);

    @Autowired
    FirestationDAO firestationDAO;

    @GetMapping(value = "/firestation")
    public List<Firestation> getFirestation() {
        return firestationDAO.getFirestations();
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