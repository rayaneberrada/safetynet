package fr.openclassroom.safetynet.DAO;

import fr.openclassroom.safetynet.DTO.JsonFileDTO;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class FilterDAOTestIT {

    private static FilterDAO filterDAO;
    private static JsonFileDTO jsonFileDTO;

    @Before
    public void setUp() throws IOException {
        jsonFileDTO = JsonFileDTO.getInstance();
        filterDAO = new FilterDAO(jsonFileDTO);
    }

    @Test
    public void getPersonsEmailInCityTest() {

    }
}
