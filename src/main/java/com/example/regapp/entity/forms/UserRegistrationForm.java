package com.example.regapp.entity.forms;

import lombok.Data;

@Data
public class UserRegistrationForm {
    private String identifier;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
}
