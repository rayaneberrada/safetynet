package fr.openclassroom.safetynet.DAO;

import fr.openclassroom.safetynet.DTO.JsonFileDTO;
import fr.openclassroom.safetynet.beans.Person;
import fr.openclassroom.safetynet.controllers.PersonController;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Class managing crud methods for Person related datas */
@Repository
public class PersonDAOImpl implements PersonDAO {

    private static Logger logger = LoggerFactory.getLogger(PersonDAOImpl.class);

    public Map<String, Person> persons;

    @Autowired
    public PersonDAOImpl(JsonFileDTO jsonFileDTO) {
        try {
            this.persons = jsonFileDTO.getPersons();
            logger.info("Classe de gestion des personnes initialisée");
        } catch (NullPointerException | IOException e) {
            logger.error("echec pour initialiser PersonDAOImpl", e);
        }
    }

    /**
     *
     * @return all persons datas in a list
     */
    @Override
    public List<Person> getPersons() {
        return new ArrayList<>(this.persons.values());
    }

    /**
     * This method take the person used in parameter and look for it in the datas using
     * it's first name and last name. Then replace the informations with the one sent in parameter.
     *
     * @param personToUpdate
     * @return all persons datas in a list after modification
     */
    @Override
    public List<Person> updatePerson(Person personToUpdate) {
        for (Person person : this.persons.values()) {
            if (personToUpdate.getFirstName().equals(person.getFirstName())
                    && personToUpdate.getLastName().equals(person.getLastName())) {
                person.setAddress(personToUpdate.getAddress());
                person.setCity(personToUpdate.getCity());
                person.setEmail(personToUpdate.getEmail());
                person.setPhone(personToUpdate.getPhone());
                person.setZip(personToUpdate.getZip());
            }
        }
        return new ArrayList<>(this.persons.values());
    }

    /**
     * This method take the person used in parameter and look for it in the datas using
     * it's first name and last name. Then add the informations to the ones already existing.
     *
     * @param person
     * @return all persons datas in a list after modification
     */
    @Override
    public List<Person> addPerson(Person person) {
        logger.info(String.valueOf(person.getFirstName() + " " + person.getLastName()));
        try {
            this.persons.put(person.getFirstName() + " " + person.getLastName(), person);
            logger.info(person.getFirstName() + " " + person.getLastName() + " a bien été ajouté");
        } catch (Exception e) {
            logger.error("Impossible d'ajouter la personne envoyée", e);
        }
        return new ArrayList<>(this.persons.values());
    }

    /**
     * This method take the person used in parameter and look for it in the datas using
     * it's first name and last name. Then delete the informations if the person exist.
     *
     * @param personToDelete
     * @return all persons datas in a list after modification
     */
    @Override
    public List<Person> deletePerson(Person personToDelete) {
        boolean deleted = this.persons.values().removeIf(person -> personToDelete.getFirstName().equals(person.getFirstName())
                && personToDelete.getLastName().equals(person.getLastName()));
        if (deleted) {
            logger.info(personToDelete.getFirstName() + " " + personToDelete.getLastName() + " a bien été supprimé");
            logger.info("il y a désormais " + persons.size() + " personnes dans la liste");
        } else {
            logger.error("Aucune personne ne correspond à " + personToDelete.getFirstName() + " " + personToDelete.getLastName());
        }
        return new ArrayList<>(this.persons.values());
    }
}

