package fr.openclassroom.safetynet.controllers;

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

    @Autowired
    MedicalRecordDAO medicalRecordDAOImpl;

    @GetMapping(value = "/medicalrecord")
    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecordDAOImpl.getMedicalRecords();
    }

    @DeleteMapping("/medicalrecord")
    public List<MedicalRecord> removePerson(@RequestBody MedicalRecord medicalRecord) {
        logger.info(String.valueOf(medicalRecord));
        return medicalRecordDAOImpl.deleteMedicalRecord(medicalRecord);
    }

    @PostMapping("/medicalrecord")
    public List<MedicalRecord> addPerson(@RequestBody MedicalRecord medicalRecord) throws IOException, ParseException {
        logger.info(String.valueOf(medicalRecord));
        return medicalRecordDAOImpl.addMedicalRecord(medicalRecord);
    }

    @PutMapping("/medicalrecord")
    public List<MedicalRecord> updatePerson(@RequestBody MedicalRecord medicalRecord) throws IOException, ParseException {
        logger.info(String.valueOf(medicalRecord));
        return medicalRecordDAOImpl.updateMedicalRecord(medicalRecord);
    }
}
