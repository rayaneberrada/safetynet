package fr.openclassroom.safetynet.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MedicalRecord {
    String firstName;
    String lastName;
    String birthdate;
    List<String> medications;
    List<String> allergies;
}
