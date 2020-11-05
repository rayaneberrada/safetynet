package fr.openclassroom.safetynet.DAO;

import fr.openclassroom.safetynet.beans.Firestation;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface FirestationDAO {
    List<Firestation> updateFirestation(Firestation fireStation) throws IOException, ParseException;
    List<Firestation> addFirestation(Firestation fireStation) throws IOException, ParseException;
    List<Firestation> deleteFirestation(Firestation fireStation);
    List<Firestation> getFirestations();
}
