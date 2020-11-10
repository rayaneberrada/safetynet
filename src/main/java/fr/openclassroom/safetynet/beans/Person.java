package fr.openclassroom.safetynet.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;

@Getter
@Setter
@ToString
public class Person {
    @NonNull
    String firstName;
    @NonNull
    String lastName;
    String address;
    String city;
    String zip;
    String phone;
    String email;
}
