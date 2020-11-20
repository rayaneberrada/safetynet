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

@RestController
public class MedicalRecordController {

    private static Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static FilterProvider medicalFilter = new SimpleFilterProvider().addFilter("medicalFilter", SimpleBeanPropertyFilter.serializeAll());

    @Autowired
    MedicalRecordDAO medicalRecordDAOImpl;

    @GetMapping(value = "/medicalrecord")
    public String getMedicalRecords() throws JsonProcessingException {
        return  mapper.writer(medicalFilter).withDefaultPrettyPrinter().writeValueAsString(medicalRecordDAOImpl.getMedicalRecords());
    }

    @DeleteMapping("/medicalrecord")
    public String removePerson(@RequestBody MedicalRecord medicalRecord) throws JsonProcessingException {
        logger.info(String.valueOf(medicalRecord));
        return mapper.writer(medicalFilter).withDefaultPrettyPrinter().writeValueAsString(medicalRecordDAOImpl.deleteMedicalRecord(medicalRecord));
    }

    @PostMapping("/medicalrecord")
    public String addPerson(@RequestBody MedicalRecord medicalRecord) throws IOException, ParseException {
        logger.info(String.valueOf(medicalRecord));
        return  mapper.writer(medicalFilter).withDefaultPrettyPrinter().writeValueAsString(medicalRecordDAOImpl.addMedicalRecord(medicalRecord));
    }

    @PutMapping("/medicalrecord")
    public String updatePerson(@RequestBody MedicalRecord medicalRecord) throws IOException, ParseException {
        logger.info(String.valueOf(medicalRecord));
        return  mapper.writer(medicalFilter).withDefaultPrettyPrinter().writeValueAsString(medicalRecordDAOImpl.updateMedicalRecord(medicalRecord));
    }
}
