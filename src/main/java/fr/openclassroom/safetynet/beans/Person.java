package fr.openclassroom.safetynet.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Person {
    String firstName;
    String lastName;
    String address;
    String city;
    String zip;
    String phone;
    String email;
}
