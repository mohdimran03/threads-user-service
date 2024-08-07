package com.example.userservice.dtos;

import java.time.LocalDate;
import java.util.UUID;

public class UpdateUserDto {
    private UUID id;
    private String email;
    private String password;
    private String name;
    private LocalDate dob;
    private int age;
    private String profile_picture;
    private String bio;
    private String username;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public UUID getId () { return id; }

    public void setId (UUID uuid) { this.id = uuid; }

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

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}