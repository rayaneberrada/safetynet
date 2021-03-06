package fr.openclassroom.safetynet.DTO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.openclassroom.safetynet.beans.Firestation;
import fr.openclassroom.safetynet.beans.MedicalRecord;
import fr.openclassroom.safetynet.beans.Person;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import java.io.DataInput;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/** Singleton used to store the datas from data.json. Datas will be restored to normal every time the app is reloaded
 *  because only datas stored in JsonFileDTO are modified by the app and never datas stored inside data.json.
 * */
@Repository
public class JsonFileDTO {

    private static Logger logger = LoggerFactory.getLogger(JsonFileDTO.class);

    private static JsonFileDTO jsonFileDTOInstance;
    private static String pathToFile = "data.json";
    private JSONObject jsonObject;

    /**
     * This method make sure that if JsonFileDTO hasn't been instantiated the constructor is called and
     * return the instance of the class.
     *
     * @return an instance of JsonFileDTO
     */
    @Bean
    public static JsonFileDTO getInstance(){
        if(JsonFileDTO.jsonFileDTOInstance== null) {
            jsonFileDTOInstance = new JsonFileDTO();
        }
        return jsonFileDTOInstance;
    }

    private JsonFileDTO(){
        try{
            FileReader reader = new FileReader(pathToFile);
            JSONParser jsonParser = new JSONParser();
            jsonObject  = (JSONObject) jsonParser.parse(reader);
            logger.info("Fichier json parsé avec succès");
        } catch (FileNotFoundException e) {
            logger.error("Le fichier n'a pas été trouvé", e);
        } catch (ParseException e) {
            logger.error("Impossible de parser ce fichier", e);
        } catch (IOException e) {
            logger.error("Echec durant la lecture du fichier", e);
        }
    }

    /**
     * Method parsing persons datas in json and returning it using the related bean
     *
     * @return informations about persons stored in data.json
     */
    public Map<String, Person> getPersons() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(String.valueOf(jsonObject));
        JsonNode jsonArray = jsonNode.get("persons");
        HashMap<String, Person> persons = new HashMap<>();
        for(JsonNode person: jsonArray) {
            String personJson = mapper.writeValueAsString(person);
            persons.put(person.get("firstName").asText() + " " + person.get("lastname"), mapper.readValue(personJson, Person.class));
        }
        logger.info("Il y a " + persons.size() + " personnes dans la liste");
        if (persons.size() > 0) {
            return persons;
        }else{
            logger.error("No persons parsed");
            return null;
        }
    }

    /**
     * Method parsing firestations datas in json and returning it using the related bean
     *
     * @return informations about firestations stored in data.json
     */
    public Map<String, Firestation> getFirestations() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(String.valueOf(jsonObject));
        JsonNode jsonArray = jsonNode.get("firestations");
        HashMap<String, Firestation> firestations = new HashMap<>();
        for(JsonNode firestation: jsonArray) {
            String firestationJson = mapper.writeValueAsString(firestation);
            firestations.put(firestation.get("address").asText(), mapper.readValue(firestationJson, Firestation.class));
        }
        logger.info("Il y a " + firestations.size() + " firestations dans la liste");
        if (firestations.size() > 0) {
            return firestations;
        }else{
            logger.error("No firestation parsed");
            return null;
        }
    }

    /**
     * Method parsing medical record datas in json and returning it using the related bean
     *
     * @return informations about medical record stored in data.json
     */
    public Map<String, MedicalRecord> getMedicalRecords() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(String.valueOf(jsonObject));
        ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
        });
        JsonNode jsonArray = jsonNode.get("medicalrecords");
        HashMap <String, MedicalRecord> medicalRecords = new HashMap<>();
        for(JsonNode medicalRecordJson: jsonArray) {
            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setFirstName(medicalRecordJson.get("firstName").asText());
            medicalRecord.setLastName(medicalRecordJson.get("lastName").asText());
            medicalRecord.setBirthdate(medicalRecordJson.get("birthdate").asText());
            medicalRecord.setAllergies(reader.readValue(medicalRecordJson.get("allergies")));
            medicalRecord.setMedications(reader.readValue(medicalRecordJson.get("medications")));
            medicalRecords.put(medicalRecordJson.get("firstName").asText() + " " + medicalRecordJson.get("lastName").asText(),medicalRecord);
        }
        logger.info("Il y a " + medicalRecords.size() + " dossiers médicaux");
        if (medicalRecords.size() > 0) {
            return medicalRecords;
        }else{
            logger.error("No medical record parsed");
            return null;
        }
    }
}
