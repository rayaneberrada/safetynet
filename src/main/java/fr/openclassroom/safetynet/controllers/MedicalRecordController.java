package fr.openclassroom.safetynet.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import fr.openclassroom.safetynet.DAO.MedicalRecordDAO;
import fr.openclassroom.safetynet.DAO.MedicalRecordDAOImpl;
import fr.openclassroom.safetynet.beans.MedicalRecord;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/** Controller exposing routes managing crud operations on medical records */
@RestController
public class MedicalRecordController {

    private static Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static FilterProvider medicalFilter = new SimpleFilterProvider().addFilter("medicalFilter", SimpleBeanPropertyFilter.serializeAll());

    @Autowired
    MedicalRecordDAO medicalRecordDAOImpl;

    /**
     * Route to ask all medical records available
     *
     * @return all MedicalRecord
     */
    @GetMapping(value = "/medicalrecord")
    public String getMedicalRecords() {
        logger.info("http://localhost:8080/medicalrecord");
        String medicalRecord = null;
        try {
            medicalRecord = mapper.writer(medicalFilter).withDefaultPrettyPrinter().writeValueAsString(medicalRecordDAOImpl.getMedicalRecords());
            logger.info(medicalRecord);
        }   catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return medicalRecord;
    }

    /**
     * Route to remove a medical record from datas
     *
     * @param medicalRecord
     * @return medical records after deletion
     */
    @DeleteMapping("/medicalrecord")
    public String removePerson(@RequestBody MedicalRecord medicalRecord) {
        logger.info("http://localhost:8080/medicalrecord");
        logger.info(String.valueOf(medicalRecord));
        String medicalRecordLeft = null;
        try {
            medicalRecordLeft = mapper.writer(medicalFilter).withDefaultPrettyPrinter().writeValueAsString(medicalRecordDAOImpl.deleteMedicalRecord(medicalRecord));
            logger.info(medicalRecordLeft);
        }   catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return medicalRecordLeft;
    }

    /**
     * Route to add a medical record in datas
     *
     * @param medicalRecord
     * @return medical records after adding the record
     */
    @PostMapping("/medicalrecord")
    public String addPerson(@RequestBody MedicalRecord medicalRecord) {
        logger.info("http://localhost:8080/medicalrecord");
        logger.info(String.valueOf(medicalRecord));
        String allMedicalRecord = null;
        try {
            allMedicalRecord = mapper.writer(medicalFilter).withDefaultPrettyPrinter().writeValueAsString(medicalRecordDAOImpl.addMedicalRecord(medicalRecord));
            logger.info(allMedicalRecord);
        }   catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return allMedicalRecord;
    }

    /**
     * Route to modify a medical record in datas
     *
     * @param medicalRecord
     * @return medical records after modification
     */
    @PutMapping("/medicalrecord")
    public String updatePerson(@RequestBody MedicalRecord medicalRecord) {
        logger.info("http://localhost:8080/medicalrecord");
        logger.info(String.valueOf(medicalRecord));
        String allMedicalRecord = null;
        try {
            allMedicalRecord = mapper.writer(medicalFilter).withDefaultPrettyPrinter().writeValueAsString(medicalRecordDAOImpl.updateMedicalRecord(medicalRecord));
            logger.info(allMedicalRecord);
        }   catch (Exception e) {
            logger.error("Request failed. Exception error is: " + e);
        }
        return allMedicalRecord;
    }
}
