package fr.openclassroom.safetynet.DAO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.openclassroom.safetynet.DTO.JsonFileDTO;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterDAOTestIT {

    private static FilterDAO filterDAO;
    private static JsonFileDTO jsonFileDTO;

    @Before
    public void setUp() throws IOException {
        jsonFileDTO = JsonFileDTO.getInstance();
        filterDAO = new FilterDAO(jsonFileDTO);
    }

    @Test
    public void testGetPersonsEmailInCityTest() throws JsonProcessingException {
        //GIVEN
        String firstPersonEmailInCulver = "reg@email.com";

        //WHEN
        List<String> personEmailInCulverFromMethod = filterDAO.getPersonsEmailInCity("Culver").get("Culver");

        //THEN
        Assertions.assertThat(firstPersonEmailInCulver).isEqualTo(personEmailInCulverFromMethod.get(0));
    }

    @Test
    public void testGetPersonFiltered() throws JsonProcessingException, ParseException {
        //GIVEN
        String johnBoydInfos = "{\"address\":\"1509 Culver St\",\"email\":\"jaboyd@email.com\",\"John Boyd\":{\"medications\":[\"aznol:350mg\",\"hydrapermazol:100mg\"],\"allergies\":[\"nillacilan\"]},\"âge: \":36}";

        //WHEN
        List<JsonNode> getJohnBoydFiltered = filterDAO.getPersonFiltered("John", "Boyd");

        //THEN
        Assertions.assertThat(johnBoydInfos).isEqualTo(getJohnBoydFiltered.get(0).toString());
    }

    @Test
    public void testCountAdultAndChildPerStation() throws ParseException, JsonProcessingException {
        //GIVEN
        int personsOverEighteen = 5;
        int personsUnderEighteen = 1;

        //WHEN
        Map<String,Object> adultAndChildPerStation = filterDAO.countAdultAndChildPerStation(1);

        //THEN
        Assertions.assertThat(personsOverEighteen).isEqualTo(adultAndChildPerStation.get("personsOverEighteen"));
        Assertions.assertThat(personsUnderEighteen).isEqualTo(adultAndChildPerStation.get("personsUnderEighteen"));
    }

    @Test
    public void testCountChildsAtAddress() throws ParseException, JsonProcessingException {
        //GIVEN
        int childsAtAddress = 2;

        //WHEN
        Map<String, List> personsAtAddress = filterDAO.countChildsAtAddress("1509 Culver St");

        //THEN
        Assertions.assertThat(childsAtAddress).isEqualTo(personsAtAddress.get("childsAtAddress").size());
    }

    @Test
    public void testGetPhoneNumbersForStation() throws JsonProcessingException {
        //GIVEN
        int phoneNumbers = 6;

        //WHEN
        Map<String, List<String>> phoneNumbersAtStation = filterDAO.getPhoneNumbersForStation(1);

        //THEN
        Assertions.assertThat(phoneNumbers).isEqualTo(phoneNumbersAtStation.get("Firestation n°1").size());
    }

    @Test
    public void testGetPersonsMedicalRecordsAndStationNumberOfAddress() throws JsonProcessingException {
        //GIVEN
        int personsLivingAtAddress = 5;
        String stationNumber = "3";
        List<String> johnBoydMedications = List.of("aznol:350mg","hydrapermazol:100mg");

        //WHEN
        Map<String, Object> personsAndMedicalRecordsAndStationNumber = filterDAO.getPersonsMedicalRecordsAndStationNumberOfAddress("1509 Culver St");

        //THEN
        Assertions.assertThat(personsLivingAtAddress).isEqualTo(((ArrayList)personsAndMedicalRecordsAndStationNumber.get("personnes vivant à l'adresse: ")).size());
        Assertions.assertThat(stationNumber).isEqualTo(personsAndMedicalRecordsAndStationNumber.get("numéro caserne: "));
    }

    @Test
    public void testPersonsAndMedicalRecordPerAddressPerStation() throws JsonProcessingException, ParseException {
        //GIVEN
        int personsLivingAtFirstLoneTree = 1;
        String namePersonLivingAtLoneTree = "Eric";

        //WHEN
        Map<String, List<JsonNode>> personsAndMedicalRecordForStationsOneAndTwo = filterDAO.getPersonsAndMedicalRecordPerAddressPerStation(new String[]{"1", "2"});

        //THEN
        Assertions.assertThat(personsLivingAtFirstLoneTree).isEqualTo(personsAndMedicalRecordForStationsOneAndTwo.get("951 LoneTree Rd").size());
        Assertions.assertThat(namePersonLivingAtLoneTree).isEqualTo(personsAndMedicalRecordForStationsOneAndTwo.get("951 LoneTree Rd").get(0).get("firstName").textValue());
    }
}
