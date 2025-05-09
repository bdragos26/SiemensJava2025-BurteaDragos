package com.siemens.internship.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Ensures that the name field is not blank and provides a validation message if it is missing.
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;
    private String status;

    // Validates that the email field contains a properly formatted email address.
    // If the format is invalid, a validation message is provided.
    @Email(message = "Invalid email format")
    private String email;
}