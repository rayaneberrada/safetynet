package fr.openclassroom.safetynet.DAO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.openclassroom.safetynet.DTO.JsonFileDTO;
import fr.openclassroom.safetynet.beans.Firestation;
import fr.openclassroom.safetynet.beans.MedicalRecord;
import fr.openclassroom.safetynet.beans.Person;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class FirestationDAOTest {

    private static Logger logger = LoggerFactory.getLogger(JsonFileDTO.class);

    @InjectMocks
    FirestationDAOImpl firestationDAO;

    @Mock
    JsonFileDTO jsonFileDTO;

    @NonNull
    private static JsonNode jsonNode;
    @NonNull
    private static ObjectMapper mapper;
    @NonNull
    private static Firestation republiqueStation;
    @NonNull
    private static Firestation cluverStation;

    @Before
    public void setUp() throws IOException, org.json.simple.parser.ParseException {
        String DUMMY_JSON =
                "{\"firestations\": " +
                        "[" +
                        "{ \"address\":\"13 avenue de la République\", \"station\":\"1\" }," +
                        "{ \"address\":\"1509 Culver St\", \"station\":\"3\" }" +
                        "]} ";

        mapper = new ObjectMapper();
        jsonNode = mapper.readTree(DUMMY_JSON);

        // Create beans using dummy json
        JsonNode jsonFirestations = jsonNode.get("firestations");
        Map<String, Firestation> firestations = new HashMap<>();
        republiqueStation = mapper.readValue(String.valueOf(jsonFirestations.get(0)), Firestation.class);
        cluverStation = mapper.readValue(String.valueOf(jsonFirestations.get(1)), Firestation.class);
        firestations.put("13 avenue de la République", republiqueStation);
        firestations.put("1509 Culver St", cluverStation);

        // Create classe with mocked datas
        when(jsonFileDTO.getFirestations()).thenReturn(firestations);
        firestationDAO = new FirestationDAOImpl(jsonFileDTO);

    }

    @Test
    public void getFirestationTest() throws JsonProcessingException {
        // WHEN
        List<Firestation> firestations = firestationDAO.getFirestations();

        // THEN
        Assertions.assertThat(firestations).containsExactlyInAnyOrder(republiqueStation, cluverStation);
    }

    @Test
    public void addFirestationTest() throws JsonProcessingException {
        // GIVEN
        String steppesJson = "{ \"address\":\"112 Steppes Pl\", \"station\":\"3\" }";
        Firestation steppesStation = mapper.readValue(steppesJson, Firestation.class);

        // WHEN
        firestationDAO.addFirestation(steppesStation);

        // THEN
        Assertions.assertThat(firestationDAO.firestations.values()).containsExactlyInAnyOrder(republiqueStation, cluverStation, steppesStation);
    }

    @Test
    public void removeFirestationTest() throws JsonProcessingException {
        // GIVEN
        String steppesJson = "{ \"address\":\"112 Steppes Pl\", \"station\":\"3\" }";
        Firestation steppes = mapper.readValue(steppesJson, Firestation.class);

        // WHEN
        firestationDAO.deleteFirestation(steppes);

        // THEN
        Assertions.assertThat(firestationDAO.firestations.values()).containsExactlyInAnyOrder(republiqueStation, cluverStation);
    }

    @Test
    public void updateFirestationTest() throws JsonProcessingException {
        // GIVEN
        String cluverJson = "{ \"address\":\"1509 Culver St\", \"station\":\"2\" }";
        Firestation cluver = mapper.readValue(cluverJson, Firestation.class);

        // WHEN
        List<Firestation> stations = firestationDAO.updateFirestation(cluver);

        // THEN
        Assertions.assertThat(stations.get(0).getAddress()).isEqualTo("1509 Culver St");
        Assertions.assertThat(stations.get(0).getStation()).isEqualTo("2");
    }
}
