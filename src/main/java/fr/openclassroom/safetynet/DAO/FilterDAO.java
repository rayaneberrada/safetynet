package fr.openclassroom.safetynet.DAO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import fr.openclassroom.safetynet.DAO.PersonDAOImpl;
import fr.openclassroom.safetynet.DTO.JsonFileDTO;
import fr.openclassroom.safetynet.beans.Firestation;
import fr.openclassroom.safetynet.beans.MedicalRecord;
import fr.openclassroom.safetynet.beans.Person;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/** DAO managing operations to filter specific datas that can be from one or multiple beans */
@Service
public class FilterDAO {

    private static Logger logger = LoggerFactory.getLogger(PersonDAOImpl.class);

    static Map<String, Person> persons;
    static Map<String, MedicalRecord> medicalRecords;
    static Map<String, Firestation> fireStations;
    static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public FilterDAO(JsonFileDTO jsonFileDTO) throws IOException {
        persons = jsonFileDTO.getPersons();
        medicalRecords = jsonFileDTO.getMedicalRecords();
        fireStations = jsonFileDTO.getFirestations();
    }

    /**
     * Method to filter person's email for people linving in a specific city
     *
     * @param city
     * @return person's email
     */
    public Map<String, List<String>> getPersonsEmailInCity(String city) throws JsonProcessingException {
        List<Person> personsAtCity = this.persons.values().stream().filter(person -> person.getCity().equals(city)).collect(Collectors.toList());
        logger.info("Persons in city: " + personsAtCity);
        List<String> personsEmail = personsAtCity.stream().map(person -> person.getEmail()).collect(Collectors.toList());
        return Map.of(city, personsEmail);
    }

    /**
     * Method to get a person information using it's firstname and lastname to find it
     *
     * @param firstName
     * @param lastName
     * @return person name, age, address, email and medical record
     */
    public List<JsonNode> getPersonFiltered(String firstName, String lastName) throws JsonProcessingException, ParseException {
        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("address", "email"));
        FilterProvider medicalFilter = new SimpleFilterProvider().addFilter("medicalFilter", SimpleBeanPropertyFilter.filterOutAllExcept("medications", "allergies"));

        List<Person> personsByName;
        if (firstName == null && lastName != null) {
            personsByName = this.persons.values().stream().filter(person -> (lastName).equals(person.getLastName())).collect(Collectors.toList());
        } else if (firstName != null && lastName == null) {
            personsByName = this.persons.values().stream().filter(person -> (firstName).equals(person.getFirstName())).collect(Collectors.toList());
        } else {
            personsByName = this.persons.values().stream().filter(person -> (firstName + lastName).equals(person.getFirstName() + person.getLastName())).collect(Collectors.toList());
        }
        List<JsonNode> personsJson = this.filterObjectsInJson(personsByName, personFilter);

        for (int i = 0; i < personsByName.size(); i++) {
            String fullname = personsByName.get(i).getFirstName() + " " + personsByName.get(i).getLastName();
            ((ObjectNode) personsJson.get(i)).put(fullname, this.filterObjectInJson(this.medicalRecords.get(fullname), medicalFilter));
            ((ObjectNode) personsJson.get(i)).put("âge: ", this.calculateAge(this.medicalRecords.get(fullname).getBirthdate()));
        }
        return personsJson;
    }

    /**
     * Method to count how many child and adult or covered by a specific station
     *
     * @param stationNumber
     * @return persons informations over and under eighteen
     */
    public Map<String, Object> countAdultAndChildPerStation(int stationNumber) throws ParseException, JsonProcessingException {
        HashMap<String, Object> adultAndChildPerStation = new HashMap<>();
        // Get address covered by a station
        List<String> addresses = getAddressesAtStation(stationNumber);
        logger.info("Adresse(s) récuérée(s): " + addresses);
        //Rajouter une exception et logger si addresses est vide?
        List<Person> personsAtAdresses = this.persons.values()
                .stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .collect(Collectors.toList());
        logger.info("Nombre de personnes aux adresses: " + personsAtAdresses.size());

        List<Person> personsOverEighteen = new ArrayList<>();
        List<Person> personsUnderEighteen = new ArrayList<>();
        List<JsonNode> personAtAdressesFiltered = new ArrayList<>();
        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("firstName", "lastName", "address", "phone"));
        for (Person person : personsAtAdresses) {
            String birthDay = this.medicalRecords.get(person.getFirstName() + " " + person.getLastName()).getBirthdate();
            int age = calculateAge(birthDay);
            if (age > 18) {
                personsOverEighteen.add(person);
            } else {
                personsUnderEighteen.add(person);
            }
            String personAtAdressesJson = mapper.writer(personFilter)
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(person);
            JsonNode personJsonObject = mapper.readTree(personAtAdressesJson);
            personAtAdressesFiltered.add(personJsonObject);

        }
        logger.info("Adulte(s): " + personsOverEighteen.size() + " Enfant(s): " + personsUnderEighteen.size());

        adultAndChildPerStation.put("personsAtFirestationAddress", personAtAdressesFiltered);
        adultAndChildPerStation.put("personsOverEighteen", personsOverEighteen.size());
        adultAndChildPerStation.put("personsUnderEighteen", personsUnderEighteen.size());

        return adultAndChildPerStation;
    }

    /**
     * Method to count how many child live at a specific address
     *
     * @param address
     * @return firstname, lastname, age of every child and a list of other members
     */
    public Map<String, List> countChildsAtAddress(String address) throws ParseException, JsonProcessingException {
        Map<String, List> childsAtAddress = new HashMap<>();
        List<JsonNode> childs = new ArrayList<>();
        List<JsonNode> adults = new ArrayList<>();
        logger.info(String.valueOf(this.persons));
        List<Person> personsAtAddress = this.persons.values()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
        logger.info("persons at address: " + personsAtAddress);

        List<JsonNode> childsFiltered = new ArrayList<>();
        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("firstName", "lastName"));
        for (Person person : personsAtAddress) {
            String birthDate = this.medicalRecords.get(person.getFirstName() + " " + person.getLastName()).getBirthdate();

            String personAtAdressesJson = mapper.writer(personFilter)
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(person);
            JsonNode personJsonObject = mapper.readTree(personAtAdressesJson);
            int age = calculateAge(birthDate);
            ((ObjectNode) personJsonObject).put("age", age);
            childsFiltered.add(personJsonObject);
            if (age > 18) {
                adults.add(personJsonObject);
            } else {
                childs.add(personJsonObject);
            }


        }
        childsAtAddress.put("childsAtAddress", childs);
        childsAtAddress.put("otherMembers", adults);

        return childsAtAddress;
    }

    /**
     * Method to get phone numbers for people covered by a station
     *
     * @param stationNumber
     * @return phone numbers
     */
    public Map<String, List<String>> getPhoneNumbersForStation(int stationNumber) throws JsonProcessingException {
        List<String> addresses = this.getAddressesAtStation(stationNumber);
        List<List<Person>> personsAtAdresses = addresses.stream().map(address -> this.getPersonsAtAddress(address)).collect(Collectors.toList());


        List<String> phoneNumbers = new ArrayList<>();
        for (List<Person> personsAtAddress : personsAtAdresses) {
            personsAtAddress.stream().forEach(person -> phoneNumbers.add(person.getPhone()));
        }
        logger.info("Nombre de personnes aux adresses: " + personsAtAdresses.size());
        return Map.of("Firestation n°" + stationNumber, phoneNumbers);
    }

    /**
     * Method to get medical record of people living at an address and the station number covering
     * the address
     *
     * @param address
     * @return
     */
    public Map<String, Object> getPersonsMedicalRecordsAndStationNumberOfAddress(String address) throws JsonProcessingException {
        //Modifier en utilisant des maps pour avoir une meilleure visibilitée
        Map<String, Object> personAndMedicalRecordsAndFirestation = new HashMap<>();
        Map<String, MedicalRecord> medicalRecords = this.medicalRecords;
        List<Person> personsAtAddress = getPersonsAtAddress(address);
        logger.info("People living at address requested: " + personsAtAddress.toString());

        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("phone"));
        FilterProvider medicalFilter = new SimpleFilterProvider().addFilter("medicalFilter", SimpleBeanPropertyFilter.filterOutAllExcept("medications", "allergies"));

        List<JsonNode> personAndMedicalRecord = new ArrayList<>();
        for (Person person : personsAtAddress) {
            String fullName = person.getFirstName() + " " + person.getLastName();
            String personJson = mapper.writer(personFilter)
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(person);
            String medicalJson = mapper.writer(medicalFilter)
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(medicalRecords.get(fullName));

            JsonNode personJsonNode = mapper.readTree(personJson);
            JsonNode medicalJsonNode = mapper.readTree(medicalJson);
            ((ObjectNode) personJsonNode).put(fullName, medicalJsonNode);

            personAndMedicalRecord.add(personJsonNode);
        }
        personAndMedicalRecordsAndFirestation.put("personnes vivant à l'adresse: ", personAndMedicalRecord);
        personAndMedicalRecordsAndFirestation.put("numéro caserne: ", this.fireStations.values()
                .stream()
                .filter(firestation -> firestation.getAddress().equals(address))
                .collect(Collectors.toList()).get(0).getStation());

        return personAndMedicalRecordsAndFirestation;
    }

    /**
     * Method to get person's medical records for the addresses covered by one or mulitple stations
     *
     * @param stations
     */
    public Map<String, List<JsonNode>> getPersonsAndMedicalRecordPerAddressPerStation(String[] stations) throws JsonProcessingException, ParseException {
        logger.info("Les stations requêtées sont: " + Arrays.asList(stations));
        Map<String, List<JsonNode>> PersonsAndMedicalRecordPerAddressPerStation = new HashMap<>();
        List<String> firestationsAddresses = this.fireStations.values().stream()
                .filter(firestation -> Arrays.asList(stations).contains(firestation.getStation()))
                .map(firestation -> firestation.getAddress())
                .collect(Collectors.toList());
        logger.info("Les stations requêtées sont: " + firestationsAddresses);

        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("phone", "firstName", "lastName"));
        FilterProvider medicalFilter = new SimpleFilterProvider().addFilter("medicalFilter", SimpleBeanPropertyFilter.filterOutAllExcept("medications", "allergies"));
        for (String address : firestationsAddresses) {
            List<Person> personsAtAddress = this.getPersonsAtAddress(address);
            List<JsonNode> personsJson = this.filterObjectsInJson(personsAtAddress, personFilter);

            for (JsonNode person : personsJson) {
                String fullname = person.get("firstName").asText() + " " + person.get("lastName").asText();
                ((ObjectNode) person).put(fullname, this.filterObjectInJson(this.medicalRecords.get(fullname), medicalFilter));
                ((ObjectNode) person).put("âge: ", this.calculateAge(this.medicalRecords.get(fullname).getBirthdate()));
            }
            PersonsAndMedicalRecordPerAddressPerStation.put(address, personsJson);
        }

        return PersonsAndMedicalRecordPerAddressPerStation;
    }

    /**
     * Method used to transform a list of Object into a list of JsonNode
     *
     * @param objectsToFilter
     * @param filter
     */
    static List<JsonNode> filterObjectsInJson(List objectsToFilter, FilterProvider filter) throws JsonProcessingException {
        List<JsonNode> objectsJson = new ArrayList<>();
        for (Object object : objectsToFilter) {
            objectsJson.add(filterObjectInJson(object, filter));
        }
        return objectsJson;
    }

    /**
     * Method used to transform an Object into a JsonNode
     *
     * @param objectToFilter
     * @param filter
     */
    static JsonNode filterObjectInJson(Object objectToFilter, FilterProvider filter) throws JsonProcessingException {
        String objectJson = mapper.writer(filter)
                .withDefaultPrettyPrinter()
                .writeValueAsString(objectToFilter);
        return mapper.readTree(objectJson);
    }

    static List<Person> getPersonsAtAddress(String address) {
        List<Person> personAtAddress = new ArrayList<>();
        try {
            personAtAddress = persons.values()
                    .stream()
                    .filter(person -> address.equals(person.getAddress()))
                    .collect(Collectors.toList());

        } catch (NullPointerException e) {
            logger.error("Nothing is found for this address: " + address);
        } catch (Exception e) {
            logger.error("Unkknown error");
        }
        return personAtAddress;
    }

    static List<String> getAddressesAtStation(int stationNumber) {
        return fireStations.values()
                .stream()
                .filter(station -> Integer.parseInt(station.getStation()) == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
    }

    static int calculateAge(String birthDay) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.FRANCE);
        LocalDate birthDate = df.parse(birthDay).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String todayString = df.format(new Date());
        LocalDate today = df.parse(todayString).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int age = Period.between(birthDate, today).getYears();
        return age;
    }
}
