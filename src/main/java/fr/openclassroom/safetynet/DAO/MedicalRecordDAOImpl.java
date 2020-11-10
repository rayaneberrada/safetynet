package fr.openclassroom.safetynet.DAO;

import fr.openclassroom.safetynet.DTO.JsonFileDTO;
import fr.openclassroom.safetynet.beans.MedicalRecord;
import fr.openclassroom.safetynet.beans.Person;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MedicalRecordDAOImpl implements MedicalRecordDAO{

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordDAOImpl.class);

    public Map<String, MedicalRecord> medicalRecords;

    @Autowired
    public MedicalRecordDAOImpl(JsonFileDTO jsonFileDTO){
        try {
            this.medicalRecords = jsonFileDTO.getMedicalRecords();
            logger.info("Classe de gestion des personnes initialisée");
        } catch (NullPointerException | IOException e) {
            logger.error("echec pour initialiser MedicalRecordsDAOImpl", e);
        }
    }

    @Override
    public List<MedicalRecord> updateMedicalRecord(MedicalRecord recordToUpdate){
        for(MedicalRecord medicalRecord: this.medicalRecords.values()) {
            if(recordToUpdate.getFirstName().equals(medicalRecord.getFirstName())
                    && recordToUpdate.getLastName().equals(medicalRecord.getLastName())) {
                medicalRecord.setBirthdate(recordToUpdate.getBirthdate());
                medicalRecord.setMedications(recordToUpdate.getMedications());
                medicalRecord.setAllergies(recordToUpdate.getAllergies());
            }
        }
        return new ArrayList<>(this.medicalRecords.values());
    }

    @Override
    public List<MedicalRecord> addMedicalRecord(MedicalRecord recordToAdd){
        try{
            this.medicalRecords.put(recordToAdd.getFirstName() + " " + recordToAdd.getLastName(),recordToAdd);
            logger.info(recordToAdd.getFirstName() + " " + recordToAdd.getLastName() + " a bien été ajouté");
        } catch (Exception e){
            logger.error("Impossible d'ajouter la personne envoyée", e);
        }
        return new ArrayList<>(this.medicalRecords.values());
    }

    @Override
    public List<MedicalRecord> deleteMedicalRecord(MedicalRecord recordToDelete) {
        boolean deleted = this.medicalRecords.values().removeIf(person -> recordToDelete.getFirstName().equals(person.getFirstName())
                && recordToDelete.getLastName().equals(person.getLastName()));
        if (deleted) {
            logger.info(recordToDelete.getFirstName() + " " + recordToDelete.getLastName() + " a bien été supprimé");
            logger.info("il y a désormais " + medicalRecords.size() + " personnes dans la liste");
        } else {
            logger.error("Aucune personne ne correspond à " + recordToDelete.getFirstName() + " " + recordToDelete.getLastName());
        }
        return new ArrayList<>(this.medicalRecords.values());
    }

    @Override
    public List<MedicalRecord> getMedicalRecords() {
        return new ArrayList<>(this.medicalRecords.values());
    }
}
