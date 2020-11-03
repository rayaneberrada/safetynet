package fr.openclassroom.safetynet.DAO;

import fr.openclassroom.safetynet.DTO.JsonFileDTO;
import fr.openclassroom.safetynet.beans.MedicalRecord;
import fr.openclassroom.safetynet.beans.Person;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class MedicalRecordDAOImpl implements MedicalRecordDAO{

    private static Logger logger = LoggerFactory.getLogger(PersonDAOImpl.class);

    public List<Person> persons;

    @Autowired
    public MedicalRecordDAOImpl(JsonFileDTO jsonFileDTO){
        try {
            this.persons = jsonFileDTO.getPersons();
            logger.info("Classe de gestion des personnes initialisée");
        } catch (NullPointerException | IOException e) {
            logger.error("echec pour initialiser MedicalRecordsDAOImpl", e);
        }
    }

    @Override
    public List<MedicalRecord> updateMedicalRecord(MedicalRecord medicalRecord) throws IOException, ParseException {
        return null;
    }

    @Override
    public List<MedicalRecord> addMedicalRecord(MedicalRecord medicalRecord) throws IOException, ParseException {
        return null;
    }

    @Override
    public List<MedicalRecord> deleteMedicalRecord(MedicalRecord medicalRecord) {
        return null;
    }

    @Override
    public List<MedicalRecord> getMedicalRecords() {
        return null;
    }
}
