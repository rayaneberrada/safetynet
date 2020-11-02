package fr.openclassroom.safetynet.DAO;

import fr.openclassroom.safetynet.beans.Person;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface PersonDAO {
    List<Person> updatePerson(Person person) throws IOException, ParseException;
    List<Person> addPerson(Person person) throws IOException, ParseException;
    List<Person> deletePerson(Person person);
    List<Person> getPersons();
}
