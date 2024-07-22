package com.example.userservice.dtos;

public class RegisterUserDto {
    private String email;
    private String password;
    private String name;
    private String id_token;

    // Getters and Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId_token(String token) {
        this.id_token = token;
    }

    public String getId_token() {
        return id_token;
    }
}