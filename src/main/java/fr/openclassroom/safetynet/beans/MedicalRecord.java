package fr.openclassroom.safetynet.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@Setter
@ToString
public class MedicalRecord {
    @NonNull
    String firstName;
    @NonNull
    String lastName;
    String birthdate;
    List<String> medications;
    List<String> allergies;
}
