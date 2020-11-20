package fr.openclassroom.safetynet.services;

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

@Service
public class DataFilter {

    private static Logger logger = LoggerFactory.getLogger(PersonDAOImpl.class);

    private Map<String, Person> persons;
    private Map<String, MedicalRecord> medicalRecords;
    private Map<String, Firestation> fireStations;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public DataFilter(JsonFileDTO jsonFileDTO) throws IOException {
        persons = jsonFileDTO.getPersons();
        medicalRecords = jsonFileDTO.getMedicalRecords();
        fireStations = jsonFileDTO.getFirestations();
    }

    public Map<String, List<String>> getPersonsEmailInCity(String city) throws JsonProcessingException {
        List<Person> personsAtCity = this.persons.values().stream().filter(person -> person.getCity().equals(city)).collect(Collectors.toList());
        logger.info("Persons in city: " + personsAtCity );
        List<String> personsEmail = personsAtCity.stream().map(person -> person.getEmail()).collect(Collectors.toList());
        return Map.of(city, personsEmail);
    }

    public List<JsonNode> getPersonFiltered(String firstName, String lastName) throws JsonProcessingException, ParseException {
        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("address", "email"));
        FilterProvider medicalFilter = new SimpleFilterProvider().addFilter("medicalFilter", SimpleBeanPropertyFilter.filterOutAllExcept("medications", "allergies"));

        List<Person> personsByName;
        if (firstName == null && lastName != null) {
            personsByName = this.persons.values().stream().filter(person -> (lastName).equals(person.getLastName())).collect(Collectors.toList());
        } else if (firstName != null && lastName == null) {
            personsByName = this.persons.values().stream().filter(person -> (firstName).equals(person.getFirstName())).collect(Collectors.toList());
        }else{
            personsByName = this.persons.values().stream().filter(person -> (firstName + lastName).equals(person.getFirstName() + person.getLastName())).collect(Collectors.toList());
        }
        List<JsonNode> personsJson = this.filterObjectsInJson(personsByName, personFilter);

        for (int i = 0; i < personsByName.size(); i ++){
            String fullname = personsByName.get(i).getFirstName() + " " + personsByName.get(i).getLastName();
            ((ObjectNode)personsJson.get(i)).put(fullname, this.filterObjectInJson(this.medicalRecords.get(fullname), medicalFilter));
            ((ObjectNode)personsJson.get(i)).put("âge: ", this.calculateAge(this.medicalRecords.get(fullname).getBirthdate()));
        }
        return personsJson;
    }

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
        for(Person person: personsAtAdresses){
            String birthDay = this.medicalRecords.get(person.getFirstName() + " " + person.getLastName()).getBirthdate();
            int age = calculateAge(birthDay);
            if (age > 18) {
                personsOverEighteen.add(person);
            }   else {
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

    public Map<String, List>  countChildsAtAddress(String address) throws ParseException, JsonProcessingException {
        Map<String, List> childsAtAddress = new HashMap<>();
        List<JsonNode> childs = new ArrayList<>();
        List<JsonNode> adults = new ArrayList<>();
        List<Person> personsAtAddress = this.persons.values()
                                                    .stream()
                                                    .filter(person -> person.getAddress().equals(address))
                                                    .collect(Collectors.toList());

        List<JsonNode> childsFiltered = new ArrayList<>();
        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("firstName", "lastName"));
        for (Person person: personsAtAddress) {
            String birthDate = this.medicalRecords.get(person.getFirstName() + " " + person.getLastName()).getBirthdate();
            String personAtAdressesJson = mapper.writer(personFilter)
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(person);
            JsonNode personJsonObject = mapper.readTree(personAtAdressesJson);
            int age = calculateAge(birthDate);
            ((ObjectNode)personJsonObject).put("age", age);
            childsFiltered.add(personJsonObject);
            if (age > 18) {
                adults.add(personJsonObject);
            }   else {
                childs.add(personJsonObject);
            }


        }
        childsAtAddress.put("childsAtAddress", childs);
        childsAtAddress.put("otherMembers", adults);

        return childsAtAddress;
    }

    public Map<String, List<String>> getPhoneNumbersForStation(int stationNumber) throws JsonProcessingException {
        List<String> addresses = this.getAddressesAtStation(stationNumber);
        List<List<Person>> personsAtAdresses = addresses.stream().map(address -> this.getPersonsAtAddress(address)).collect(Collectors.toList());


        List<String> phoneNumbers = new ArrayList<>();
        for (List<Person> personsAtAddress: personsAtAdresses) {
            personsAtAddress.stream().forEach(person -> phoneNumbers.add(person.getPhone()));
        }
        logger.info("Nombre de personnes aux adresses: " + personsAtAdresses.size());
        return Map.of("Firestation n°" + stationNumber, phoneNumbers);
    }

    public Map<String, Object> getPersonsMedicalRecordsAndStationNumberOfAddress(String address) throws JsonProcessingException {
        //Modifier en utilisant des maps pour avoir une meilleure visibilitée
        Map<String, Object> personAndMedicalRecordsAndFirestation = new HashMap<>();
        Map<String, MedicalRecord> medicalRecords = this.medicalRecords;
        List<Person> personsAtAddress  = getPersonsAtAddress(address);
        logger.info("People living at address requested: " + personsAtAddress.toString());

        FilterProvider personFilter = new SimpleFilterProvider().addFilter("personFilter", SimpleBeanPropertyFilter.filterOutAllExcept("phone"));
        FilterProvider medicalFilter = new SimpleFilterProvider().addFilter("medicalFilter", SimpleBeanPropertyFilter.filterOutAllExcept("medications", "allergies"));

        List<JsonNode> personAndMedicalRecord = new ArrayList<>();
        for(Person person: personsAtAddress){
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
        for (String address: firestationsAddresses) {
            List<Person> personsAtAddress = this.getPersonsAtAddress(address);
            List<JsonNode> personsJson = this.filterObjectsInJson(personsAtAddress, personFilter);

            for (JsonNode person: personsJson) {
                String fullname = person.get("firstName").asText() + " " + person.get("lastName").asText();
                ((ObjectNode)person).put(fullname, this.filterObjectInJson(this.medicalRecords.get(fullname), medicalFilter));
                ((ObjectNode)person).put("âge: ", this.calculateAge(this.medicalRecords.get(fullname).getBirthdate()));
            }
            PersonsAndMedicalRecordPerAddressPerStation.put(address, personsJson);
        }

        return PersonsAndMedicalRecordPerAddressPerStation;
    }

    private List<JsonNode> filterObjectsInJson(List objectsToFilter, FilterProvider filter) throws JsonProcessingException {
        List<JsonNode> objectsJson = new ArrayList<>();
        for (Object object: objectsToFilter) {
            objectsJson.add(filterObjectInJson(object, filter));
        }
        return objectsJson;
    }

    private JsonNode filterObjectInJson(Object objectToFilter, FilterProvider filter) throws JsonProcessingException {
        String objectJson = this.mapper.writer(filter)
                .withDefaultPrettyPrinter()
                .writeValueAsString(objectToFilter);
        return mapper.readTree(objectJson);
    }

    private List<Person> getPersonsAtAddress(String address) {
        List<Person> personAtAddress = new ArrayList<>();
        try {
            personAtAddress = this.persons.values()
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

    private List<String> getAddressesAtStation(int stationNumber) {
        return this.fireStations.values()
                .stream()
                .filter(station -> Integer.parseInt(station.getStation()) == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
    }

    private int calculateAge(String birthDay) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.FRANCE);
        LocalDate birthDate = df.parse(birthDay).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String todayString = df.format(new Date());
        LocalDate today = df.parse(todayString).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int age = Period.between(birthDate, today).getYears();
        return age;
    }
}
