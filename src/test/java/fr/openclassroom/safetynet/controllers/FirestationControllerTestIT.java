package fr.openclassroom.safetynet.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.openclassroom.safetynet.beans.Firestation;
import fr.openclassroom.safetynet.beans.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class FirestationControllerTestIT {
    @Autowired
    private MockMvc mockMvc;

    static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testGetFirestations() throws Exception {
        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testGetpersonsAndMedicalRecordPerAddressPerStation() throws Exception {
        mockMvc.perform(get("/flood/stations?stations=1&stations=2"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testGetPersonsPhoneForStation() throws Exception {
        mockMvc.perform(get("/phoneAlert?stationNumber=1"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testDeleteFirestation() throws Exception {
        Firestation stationToDelete = new Firestation();
        stationToDelete.setAddress("644 Gershwin Cir");
        stationToDelete.setStation("1" );
        mockMvc.perform(delete("/firestation")
                .contentType("application/json")
                .content(mapper.writeValueAsString(stationToDelete)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testPostFirestation() throws Exception {
        Firestation stationToAdd = new Firestation();
        stationToAdd.setAddress("guy de la morandais");
        stationToAdd.setStation("2");
        mockMvc.perform(post("/firestation")
                .contentType("application/json")
                .content(mapper.writeValueAsString(stationToAdd)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testPutFirestation() throws Exception {
        Firestation stationToModify = new Firestation();
        stationToModify.setAddress("guy de la morandais");
        stationToModify.setStation("3");
        mockMvc.perform(post("/firestation")
                .contentType("application/json")
                .content(mapper.writeValueAsString(stationToModify)))
                .andExpect(status().is2xxSuccessful());
    }
}
