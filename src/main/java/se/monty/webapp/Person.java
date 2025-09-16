package se.monty.webapp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Person {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String name;
    @NotBlank private String country;

    public Person() {}
    public Person(String name, String country) { this.name = name; this.country = country; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public void setName(String name) { this.name = name; }
    public void setCountry(String country) { this.country = country; }
}
