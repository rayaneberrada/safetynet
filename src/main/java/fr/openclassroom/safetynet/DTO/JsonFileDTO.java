package fr.openclassroom.safetynet.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.openclassroom.safetynet.beans.Person;
import fr.openclassroom.safetynet.controllers.PersonController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import java.io.DataInput;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class JsonFileDTO {

    private static Logger logger = LoggerFactory.getLogger(JsonFileDTO.class);

    private static JsonFileDTO jsonFileDTOInstance;
    private static String pathToFile = "data.json";
    private JSONObject jsonObject;

    @Bean
    public static JsonFileDTO getInstance(){
        if(JsonFileDTO.jsonFileDTOInstance== null) {
            jsonFileDTOInstance = new JsonFileDTO();
        }
        return jsonFileDTOInstance;
    }

    public JsonFileDTO(){
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

    public List<Person> getPersons() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(String.valueOf(jsonObject));
        JsonNode jsonArray = jsonNode.get("persons");
        List<Person> persons = new ArrayList<>();
        for(JsonNode person: jsonArray) {
            String personJson = mapper.writeValueAsString(person);
            persons.add(mapper.readValue(personJson, Person.class));
        }
        logger.info("Il y a " + persons.size() + " personnes dans la liste");
        return persons;
    }
}