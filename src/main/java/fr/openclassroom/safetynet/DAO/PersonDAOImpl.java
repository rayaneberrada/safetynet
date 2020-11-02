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
import java.util.List;

@Repository
public class PersonDAOImpl implements PersonDAO{

    private static Logger logger = LoggerFactory.getLogger(PersonDAOImpl.class);

    public List<Person> persons;

    @Autowired
    public PersonDAOImpl(JsonFileDTO jsonFileDTO){
        try {
            this.persons = jsonFileDTO.getPersons();
            logger.info("Classe de gestion des personnes initialisée");
        } catch (NullPointerException | IOException e) {
            logger.error("echec pour initialiser PersonDAOImpl", e);
        }
    }

    @Override
    public List<Person> getPersons() {
        return this.persons;
    }

    @Override
    public List<Person> updatePerson(Person personToUpdate){
        for(Person person: this.persons) {
            if(personToUpdate.getFirstName().equals(person.getFirstName())
                    && personToUpdate.getLastName().equals(person.getLastName())) {
                person.setAddress(personToUpdate.getAddress());
                person.setCity(personToUpdate.getCity());
                person.setEmail(personToUpdate.getEmail());
                person.setPhone(personToUpdate.getPhone());
                person.setZip(personToUpdate.getZip());
            }
        }
        return this.persons;
    }

    @Override
    public List<Person> addPerson(Person person){
        boolean added = this.persons.add(person);
        if (added) {
            logger.info(person + " a bien été ajouté");
        }   else {
            logger.error("Impossible d'ajouter la personne envoyée");
        }
        return this.persons;
    }

    @Override
    public List<Person> deletePerson(Person personToDelete) {
        boolean deleted = this.persons.removeIf(person -> personToDelete.getFirstName().equals(person.getFirstName())
                && personToDelete.getLastName().equals(person.getLastName()));
        if (deleted) {
            logger.info(personToDelete.getFirstName() + " " + personToDelete.getLastName() + " a bien été supprimé");
            logger.info("il y a désormais " + persons.size() + " personnes dans la liste");
        } else {
            logger.error("Aucune personne ne correspond à " + personToDelete.getFirstName() + " " + personToDelete.getLastName());
        }
        return this.persons;
    }
}

