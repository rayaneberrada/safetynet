package fr.openclassroom.safetynet.DAO;

import fr.openclassroom.safetynet.DTO.JsonFileDTO;
import fr.openclassroom.safetynet.beans.Firestation;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Class managing crud methods for Firestation related datas */
@Repository
public class FirestationDAOImpl implements FirestationDAO{

    private static Logger logger = LoggerFactory.getLogger(FirestationDAOImpl.class);

    public Map<String, Firestation> firestations;

    @Autowired
    public FirestationDAOImpl(JsonFileDTO jsonFileDTO){
        try {
            this.firestations = jsonFileDTO.getFirestations();
            logger.info("Classe de gestion des personnes initialisée");
        } catch (NullPointerException | IOException e) {
            logger.error("echec pour initialiser FirestationsDAOImpl", e);
        }
    }

    /**
     * This method take the statoin used in parameter and look for it in the datas using
     * it's first address. Then modify the station with the new informations.
     *
     * @param stationUpdate
     * @return all stations datas in a list after modification
     */
    @Override
    public List<Firestation> updateFirestation(Firestation stationUpdate){
        for(Firestation firestation: this.firestations.values()) {
            if(firestation.getAddress().equals(stationUpdate.getAddress())){
                firestation.setStation(stationUpdate.getStation());
            }
        }
        return new ArrayList<>(this.firestations.values());
    }

    /**
     * This method take the station used in parameter and look for it in the datas using
     * it's address. Then add the station to the already existing ones.
     *
     * @param stationToAdd
     * @return all stations datas in a list after modification
     */
    @Override
    public List<Firestation> addFirestation(Firestation stationToAdd){
        try {
            this.firestations.put(stationToAdd.getAddress(), stationToAdd);
            logger.info(stationToAdd.getAddress() + " a bien été ajouté");
        }   catch(Exception e) {
            logger.error("Impossible d'ajouter la personne envoyée", e);
        }
        return new ArrayList<>(this.firestations.values());
    }

    /**
     * This method take the station used in parameter and look for it in the datas using
     * it's address. Then delete the station if found.
     *
     * @param stationToDelete
     * @return all stations datas in a list after modification
     */
    @Override
    public List<Firestation> deleteFirestation(Firestation stationToDelete) {
        boolean deleted = this.firestations.values().removeIf(firestation -> stationToDelete.getAddress().equals(firestation.getAddress()));
        if (deleted) {
            logger.info(stationToDelete.getAddress() +  " a bien été supprimé");
            logger.info("il y a désormais " + firestations.size() + " stationnes dans la liste");
        } else {
            logger.error("Aucune station ne ne correspond à " + stationToDelete.getAddress());
        }
        return new ArrayList<>(this.firestations.values());
    }

    /**
     *
     * @return all stations datas
     */
    @Override
    public List<Firestation> getFirestations() {
        return new ArrayList<>(this.firestations.values());
    }
}