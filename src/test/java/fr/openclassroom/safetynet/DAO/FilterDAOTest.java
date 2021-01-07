package fr.openclassroom.safetynet.DAO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import fr.openclassroom.safetynet.DAO.FilterDAO;
import fr.openclassroom.safetynet.DTO.JsonFileDTO;
import fr.openclassroom.safetynet.beans.Firestation;
import fr.openclassroom.safetynet.beans.MedicalRecord;
import fr.openclassroom.safetynet.beans.Person;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class FilterDAOTest {

    private static Logger logger = LoggerFactory.getLogger(JsonFileDTO.class);

    @InjectMocks
    FilterDAO filterDAO;

    @Mock
    JsonFileDTO jsonFileDTO;

    @NonNull
    private static JsonNode jsonNode;
    @NonNull
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws IOException, org.json.simple.parser.ParseException {
        String DUMMY_JSON = "{\n" +
                "\"persons\": " +
                "[\n" +
                "{ \"firstName\":\"John\", \"lastName\":\"Boyd\", \"address\":\"13 avenue de la République\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6512\", \"email\":\"jaboyd@email.com\" }," +
                "{ \"firstName\":\"Jacob\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6513\", \"email\":\"drk@email.com\" }, " +
                "{ \"firstName\":\"Tenley\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6512\", \"email\":\"tenz@email.com\" }" +
                "]," +
                "\"firestations\": " +
                "[" +
                "{ \"address\":\"13 avenue de la République\", \"station\":\"1\" }," +
                "{ \"address\":\"1509 Culver St\", \"station\":\"3\" }" +
                "], " +
                "\"medicalrecords\": " +
                "[" +
                "{ \"firstName\":\"John\", \"lastName\":\"Boyd\", \"birthdate\":\"03/06/1984\", \"medications\":[\"aznol:350mg\", \"hydrapermazol:100mg\"], \"allergies\":[\"nillacilan\"] }," +
                "{ \"firstName\":\"Jacob\", \"lastName\":\"Boyd\", \"birthdate\":\"03/06/1989\", \"medications\":[\"pharmacol:5000mg\", \"terazine:10mg\", \"noznazol:250mg\"], \"allergies\":[] },\n" +
                "{ \"firstName\":\"Tenley\", \"lastName\":\"Boyd\", \"birthdate\":\"02/18/2012\", \"medications\":[], \"allergies\":[\"peanut\"] }" +
                "]" +
                "}";
        mapper = new ObjectMapper();
        jsonNode = mapper.readTree(DUMMY_JSON);

        // Create beans using dummy json
        JsonNode jsonFirestations = jsonNode.get("firestations");
        Firestation republiqueStation = mapper.readValue(String.valueOf(jsonFirestations.get(0)), Firestation.class);
        Firestation cluverStation = mapper.readValue(String.valueOf(jsonFirestations.get(1)), Firestation.class);

        JsonNode jsonPersons = jsonNode.get("persons");
        Person john = mapper.readValue(String.valueOf(jsonPersons.get(0)), Person.class);
        Person jacob = mapper.readValue(String.valueOf(jsonPersons.get(1)), Person.class);
        Person tenley = mapper.readValue(String.valueOf(jsonPersons.get(2)), Person.class);

        JsonNode jsonMedical = jsonNode.get("medicalrecords");
        MedicalRecord johnRecord = mapper.readValue(String.valueOf(jsonMedical.get(0)), MedicalRecord.class);
        MedicalRecord jacobRecord = mapper.readValue(String.valueOf(jsonMedical.get(1)), MedicalRecord.class);
        MedicalRecord tenleyRecord = mapper.readValue(String.valueOf(jsonMedical.get(2)), MedicalRecord.class);

        // Create classe with mocked datas
        when(jsonFileDTO.getFirestations()).thenReturn(Map.of("13 avenue de la République", republiqueStation, "1509 Culver St", cluverStation));
        when(jsonFileDTO.getPersons()).thenReturn(Map.of("John Boyd", john, "Jacob Boyd", jacob, "Tenley Boyd", tenley));
        when(jsonFileDTO.getMedicalRecords()).thenReturn(Map.of("John Boyd", johnRecord, "Jacob Boyd", jacobRecord, "Tenley Boyd", tenleyRecord));
        filterDAO = new FilterDAO(jsonFileDTO);
    }

    @Test
    public void getPersonsEmailInCityTest() throws JsonProcessingException {
        // GIVEN

        // WHEN
        Map<String, List<String>> personEmailIncity = filterDAO.getPersonsEmailInCity("Culver");

        // THEN
        Assertions.assertThat(personEmailIncity.get("Culver")).contains("jaboyd@email.com");
    }

    @Test
    public void getPersonFilteredByFullNameTest() throws JsonProcessingException, ParseException {
        // GIVEN
        JsonNode johnJson = mapper.readTree("{\"address\":\"13 avenue de la République\",\"email\":\"jaboyd@email.com\",\"John Boyd\":{\"medications\":[\"aznol:350mg\",\"hydrapermazol:100mg\"],\"allergies\":[\"nillacilan\"]},\"âge: \":36}");

        // WHEN
        List<JsonNode> johnFiltered = filterDAO.getPersonFiltered("John", "Boyd");

        // THEN
        Assertions.assertThat(johnFiltered).contains(johnJson);
    }

    @Test
    public void countAdultAndChildPerStationTest() throws ParseException, JsonProcessingException {
        // GIVEN
        JsonNode johnJson = mapper.readTree("{ \"firstName\":\"John\", \"lastName\":\"Boyd\", \"address\":\"13 avenue de la République\",\"phone\":\"841-874-6512\"}");

        // WHEN
        Map<String, Object> adultAndChildPerStation = filterDAO.countAdultAndChildPerStation(1);

        // THEN
        Assertions.assertThat(adultAndChildPerStation.get("personsOverEighteen")).isEqualTo(1);
        Assertions.assertThat(adultAndChildPerStation.get("personsUnderEighteen")).isEqualTo(0);
        Assertions.assertThat((List) adultAndChildPerStation.get("personsAtFirestationAddress")).containsExactly(johnJson);

    }

    @Test
    public void countChildAtAddressTest() throws ParseException, JsonProcessingException {
        // GIVEN
        JsonNode jacobJson = mapper.readTree("{\"firstName\":\"Jacob\",\"lastName\":\"Boyd\",\"age\":31}");
        JsonNode tenleyJson = mapper.readTree("{\"firstName\":\"Tenley\",\"lastName\":\"Boyd\",\"age\":8}");

        // WHEN
        Map<String, List> childsAtAddress = filterDAO.countChildsAtAddress("1509 Culver St");
        logger.info("childs at address: " + String.valueOf(childsAtAddress));

        // THEN
        Assertions.assertThat(childsAtAddress.get("otherMembers")).containsExactly(jacobJson);
        Assertions.assertThat(childsAtAddress.get("childsAtAddress")).containsExactly(tenleyJson);
    }

    @Test
    public void getPhoneNumbersForStationTest() throws JsonProcessingException {
        // GIVEN
        String jacobPhone = "841-874-6512";
        String tenleyPhone = "841-874-6513";

        // WHEN
        Map<String, List<String>> phoneNumbersAtStation = filterDAO.getPhoneNumbersForStation(3);

        // THEN
        Assertions.assertThat(phoneNumbersAtStation.get("Firestation n°3")).contains(jacobPhone, tenleyPhone);
    }

    @Test
    public void getPersonsMedicalRecordsAndStationNumberOfAddressTest() throws JsonProcessingException {
        // GIVEN
        JsonNode tenleyJson = mapper.readTree("{\"phone\":\"841-874-6512\",\"Tenley Boyd\":{\"medications\":[],\"allergies\":[\"peanut\"]}}");
        JsonNode jacobJson = mapper.readTree("{\"phone\":\"841-874-6513\",\"Jacob Boyd\":{\"medications\":[\"pharmacol:5000mg\",\"terazine:10mg\",\"noznazol:250mg\"],\"allergies\":[]}}");

        // WHEN
        Map<String, Object> personsMedicalRecordsAndStationNumber = filterDAO.getPersonsMedicalRecordsAndStationNumberOfAddress("1509 Culver St");

        // THEN
        Assertions.assertThat((List) personsMedicalRecordsAndStationNumber.get("personnes vivant à l'adresse: ")).contains(tenleyJson, jacobJson);
        Assertions.assertThat(personsMedicalRecordsAndStationNumber.get("numéro caserne: ")).isEqualTo("3");
    }

    @Test
    public void getPersonsAndMedicalRecordPerAddressPerStationTest() throws JsonProcessingException, ParseException {
        // GIVEN
        String[] myStringArray = {"1", "3"};
        JsonNode jacobJson = mapper.readTree("{\"firstName\":\"Jacob\",\"lastName\":\"Boyd\",\"phone\":\"841-874-6513\",\"Jacob Boyd\":{\"medications\":[\"pharmacol:5000mg\",\"terazine:10mg\",\"noznazol:250mg\"],\"allergies\":[]},\"âge: \":31}");
        JsonNode tenleyJson = mapper.readTree("{\"firstName\":\"Tenley\",\"lastName\":\"Boyd\",\"phone\":\"841-874-6512\",\"Tenley Boyd\":{\"medications\":[],\"allergies\":[\"peanut\"]},\"âge: \":8}");
        JsonNode johnJson = mapper.readTree("{\"firstName\":\"John\",\"lastName\":\"Boyd\",\"phone\":\"841-874-6512\",\"John Boyd\":{\"medications\":[\"aznol:350mg\",\"hydrapermazol:100mg\"],\"allergies\":[\"nillacilan\"]},\"âge: \":36}]}");

        // WHEN
        Map<String, List<JsonNode>> personsAndMedicalRecordPerAddress = filterDAO.getPersonsAndMedicalRecordPerAddressPerStation(myStringArray);

        // THEN
        Assertions.assertThat(personsAndMedicalRecordPerAddress.get("1509 Culver St")).contains(jacobJson, tenleyJson);
        Assertions.assertThat(personsAndMedicalRecordPerAddress.get("13 avenue de la République")).contains(johnJson);
    }

    @Test
    public void calculateAgeTest() throws ParseException {
        // GIVEN
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.FRANCE);
        LocalDate birthDate = df.parse("12/26/1994").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String todayString = df.format(new Date());
        LocalDate today = df.parse(todayString).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // WHEN
        int age = filterDAO.calculateAge("12/26/1994");

        // THEN
        Assertions.assertThat(age).isEqualTo(Period.between(birthDate, today).getYears());
    }

    @Test
    public void getAddressAtStationTest() throws IOException {
        // WHEN
        List<String> addressAtStationOne = filterDAO.getAddressesAtStation(1);

        // THEN
        Assertions.assertThat(addressAtStationOne).isEqualTo(List.of("13 avenue de la République"));
    }


    @Test
    public void getPersonsAtAddressTest() throws IOException {
        // GIVEN
        JsonNode jsonPerson = jsonNode.get("persons");
        Person person = mapper.readValue(String.valueOf(jsonPerson.get(0)), Person.class);

        // WHEN
        List<Person> personAtAddress = filterDAO.getPersonsAtAddress("13 avenue de la République");

        // THEN
        Assertions.assertThat(String.valueOf(personAtAddress.get(0))).isEqualTo(String.valueOf(person));
    }

    @Test
    public void FailGetPersonsAtAddressTest() throws IOException {
        // GIVEN
        JsonNode jsonPerson = jsonNode.get("persons");
        Person person = mapper.readValue(String.valueOf(jsonPerson.get(0)), Person.class);

        // WHEN
        List<Person> personAtAddress = filterDAO.getPersonsAtAddress("12 avenue de la République");

        // THEN
        Assertions.assertThat(personAtAddress).isEmpty();
    }

    @Test
    public void FilterObjectInJsonContainTest() throws JsonProcessingException {
        // GIVEN
        JsonNode jsonPerson = jsonNode.get("persons");
        Person person = mapper.readValue(String.valueOf(jsonPerson.get(0)), Person.class);
        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("firstName", "lastName"));

        // WHEN
        JsonNode personNode = filterDAO.filterObjectInJson(person, personFilter);

        Assertions.assertThat(String.valueOf(personNode.get("firstName"))).isEqualTo("\"John\"");
        Assertions.assertThat(String.valueOf(personNode.get("lastName"))).isEqualTo("\"Boyd\"");
    }

    @Test
    public void FilterObjectInJsonDoesntContainTest() throws JsonProcessingException {
        // GIVEN
        JsonNode jsonPerson = jsonNode.get("persons");
        Person person = mapper.readValue(String.valueOf(jsonPerson.get(0)), Person.class);
        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("firstName", "lastName"));

        // WHEN
        JsonNode personNode = filterDAO.filterObjectInJson(person, personFilter);

        Assertions.assertThat(personNode).containsExactly(personNode.get("firstName"), personNode.get("lastName"));
    }

    @Test
    public void FilterObjecstInJsonContainTest() throws JsonProcessingException {
        // GIVEN
        JsonNode jsonPerson = jsonNode.get("persons");
        List<Person> persons = new ArrayList<>();
        Person personOne = mapper.readValue(String.valueOf(jsonPerson.get(0)), Person.class);
        Person personTwo = mapper.readValue(String.valueOf(jsonPerson.get(1)), Person.class);
        persons.add(personOne);
        persons.add(personTwo);

        JsonNode jsonPersonOne = mapper.readTree("{\"firstName\":\"John\",\"lastName\":\"Boyd\"}");
        JsonNode jsonPersonTwo = mapper.readTree("{\"firstName\":\"Jacob\",\"lastName\":\"Boyd\"}");

        FilterProvider
                personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("firstName", "lastName"));

        // WHEN
        JsonNode personsNode = filterDAO.filterObjectInJson(persons, personFilter);


        Assertions.assertThat(personsNode).containsExactly(jsonPersonOne, jsonPersonTwo);
    }
}
