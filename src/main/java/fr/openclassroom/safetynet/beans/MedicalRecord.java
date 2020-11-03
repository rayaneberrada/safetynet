package fr.openclassroom.safetynet.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MedicalRecord {
    String firstName;
    String lastName;
    String birthdate;
    List<String> medications;
    List<String> allergies;
}
