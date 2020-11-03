package fr.openclassroom.safetynet.DAO;

import fr.openclassroom.safetynet.beans.MedicalRecord;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface MedicalRecordDAO {
    List<MedicalRecord> updateMedicalRecord(MedicalRecord medicalRecord) throws IOException, ParseException;
    List<MedicalRecord> addMedicalRecord(MedicalRecord medicalRecord) throws IOException, ParseException;
    List<MedicalRecord> deleteMedicalRecord(MedicalRecord medicalRecord);
    List<MedicalRecord> getMedicalRecords();
}
