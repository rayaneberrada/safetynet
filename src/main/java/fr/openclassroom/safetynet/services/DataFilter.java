package fr.openclassroom.safetynet.services;

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
import java.util.stream.Collectors;

@Service
public class DataFilter {

    private static Logger logger = LoggerFactory.getLogger(PersonDAOImpl.class);

    private Map<String, Person> persons;
    private Map<String, MedicalRecord> medicalRecords;
    private Map<String, Firestation> fireStations;

    @Autowired
    public DataFilter(JsonFileDTO jsonFileDTO) throws IOException {
        persons = jsonFileDTO.getPersons();
        medicalRecords = jsonFileDTO.getMedicalRecords();
        fireStations = jsonFileDTO.getFirestations();
    }

    public Map<String, Object> countAdultAndChildPerStation(int stationNumber) throws ParseException {
        HashMap<String, Object> adultAndChildPerStation = new HashMap<>();
        List addresses = this.fireStations.values()
                                        .stream()
                                        .filter(station -> Integer.parseInt(station.getStation()) == stationNumber)
                                        .map(Firestation::getAddress)
                                        .collect(Collectors.toList());
        logger.info("Adresse(s) récuérée(s): " + addresses);
        //Rajouter une exception et logger si addresses est vide?
        List<Person> personsAtAdresses = this.persons.values()
                                            .stream()
                                            .filter(person -> addresses.contains(person.getAddress()))
                                            .collect(Collectors.toList());
        logger.info("Nombre de personnes aux adresses: " + personsAtAdresses.size());

        List<Person> personsOverEighteen = new ArrayList<>();
        List<Person> personsUnderEighteen = new ArrayList<>();
        for(Person person: personsAtAdresses){
            String birthDay = this.medicalRecords.get(person.getFirstName() + " " + person.getLastName()).getBirthdate();
            if (isAnAdult(birthDay)) {
                personsOverEighteen.add(person);
            }   else {
                personsUnderEighteen.add(person);
            }
        }
        logger.info("Adulte(s): " + personsOverEighteen.size() + " Enfant(s): " + personsUnderEighteen.size());

        adultAndChildPerStation.put("personsAtFirestationAddress", personsAtAdresses);
        adultAndChildPerStation.put("personsOverEighteen", personsOverEighteen.size());
        adultAndChildPerStation.put("personsUnderEighteen", personsUnderEighteen.size());

        return adultAndChildPerStation;
    }

    public Map<String, List>  countChildsAtAddress(String address) throws ParseException {
        Map<String, List> childsAtAddress = new HashMap<>();
        List<Person> childs = new ArrayList<>();
        List<Person> adults = new ArrayList<>();
        List<Person> personsAtAddress = this.persons.values()
                                                    .stream()
                                                    .filter(person -> person.getAddress().equals(address))
                                                    .collect(Collectors.toList());


        for (Person person: personsAtAddress) {
            String birthDate = this.medicalRecords.get(person.getFirstName() + " " + person.getLastName()).getBirthdate();
            if (isAnAdult(birthDate)) {
                adults.add(person);
            }   else {
                childs.add(person);
            }
        }
        childsAtAddress.put("childsAtAddress", childs);
        childsAtAddress.put("otherMembers", adults);

        return childsAtAddress;
    }

    public List<Person> getPhoneNumbersForStation(int stationNumber) {
        List addresses = this.fireStations.values()
                .stream()
                .filter(station -> Integer.parseInt(station.getStation()) == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
        List<Person> personsAtAdresses = this.persons.values()
                .stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .collect(Collectors.toList());
        logger.info("Nombre de personnes aux adresses: " + personsAtAdresses.size());
        //parser pour garder seulement le numéro et voir si on peut factoriser la partie consistant à récupérer les gens à une station donnée
        return personsAtAdresses;
    }

    public List<List<Object>> getPersonsMedicalRecordsAndStationNumberOfAddress(String address) {
        //Modifier en utilisant des maps pour avoir une meilleure visibilitée
        List<List<Object>> personAndMedicalRecordsAndFirestation = new ArrayList<>();
        Map<String, MedicalRecord> medicalRecords = this.medicalRecords;
        List<Person> personsAtAddress  = this.persons.values()
                .stream()
                .filter(person -> address.equals(person.getAddress()))
                .collect(Collectors.toList());
        logger.info("People living at address requested: " + personsAtAddress.toString());

        for(Person person: personsAtAddress){
            List<Object> personAndMedicalRecords = new ArrayList<>();
            personAndMedicalRecords.add(person);
            personAndMedicalRecords.add(medicalRecords.get(person.getFirstName() + " " + person.getLastName()));
            personAndMedicalRecordsAndFirestation.add(personAndMedicalRecords);
        }
        personAndMedicalRecordsAndFirestation.add(this.fireStations.values()
                .stream()
                .filter(firestation -> firestation.getAddress().equals(address))
                .collect(Collectors.toList()));

        return personAndMedicalRecordsAndFirestation;
    }

    public Map<String, List<Object>> getPersonsAndMedicalRecordPerAddressPerStation(List<Integer> stations){
        Map<String, List<Object>> PersonsAndMedicalRecordPerAddressPerStation = new HashMap<>();
        List<Firestation> firestationsAddresses = this.fireStations.values().stream()
                .filter(firestation -> stations.contains(firestation.getStation()))
                .collect(Collectors.toList());

        for (Firestation firestation: firestationsAddresses) {
            List<Object> personsAtAddressWIthMedicalRecord = new ArrayList<>();
            List<Person> persons = this.persons.values()
                    .stream()
                    .filter(person -> firestation.getAddress().equals(person.getAddress()))
                    .collect(Collectors.toList());

            for (Person person: persons) {
                Map<String, Object> personAndMedicalRecord = new HashMap<>();
                String fullname = person.getFirstName() + " " + person.getLastName();
                personAndMedicalRecord.put(fullname, this.medicalRecords.get(fullname));
                personAndMedicalRecord.put("téléphone: ", person.getPhone());
                personAndMedicalRecord.put("âge: ", this.medicalRecords.get(fullname).getBirthdate());
                personsAtAddressWIthMedicalRecord.add(personAndMedicalRecord);
            }

            PersonsAndMedicalRecordPerAddressPerStation.put(firestation.getAddress(), personsAtAddressWIthMedicalRecord);
        }

        return PersonsAndMedicalRecordPerAddressPerStation;
    }


    private boolean isAnAdult(String birthDay) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.FRANCE);
        LocalDate birthDate = df.parse(birthDay).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String todayString = df.format(new Date());
        LocalDate today = df.parse(todayString).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int age = Period.between(birthDate, today).getYears();
        return age > 18;
    }
}
