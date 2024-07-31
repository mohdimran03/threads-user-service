package com.example.userservice.models;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String name;

    @Column
    private LocalDate dob;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private int age;

    @Column
    private String bio;

    @Column
    private String profile_picture;

    @Column
    private String firebase_uid;

    @Column
    private String auth_provider;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    @Column(name = "roles")
    private Set<String> roles;

    @Column
    private String username;

    //constructors
    public User() {
    }

    public User(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public User(UUID id, String name, LocalDate dob, String email, int age) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.age = age;
    }

    public User(String name, LocalDate dob, String email, int age) {
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.age = age;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public void setProfile_picture(String profilePicture) { this.profile_picture = profilePicture; }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void set_bio(String bio) {
        this.bio = bio;
    }

    public void setFirebase_uid(String uid) {
        this.firebase_uid = uid;
    }

    public String getFirebase_uid() {
        return firebase_uid;
    }

    public void setAuth_provider(String uid) {
        this.firebase_uid = uid;
    }

    public String getAuth_provider() {
        return firebase_uid;
    }

    public String get_bio() {
        return bio;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public LocalDateTime getCreatedAt() { return created_at; }
    public LocalDateTime getUpdatedAt() { return updated_at; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    // Automatically set date on create
    @PrePersist
    protected void onCreate() {
        updated_at = created_at = LocalDateTime.now();
    }

    // Automatically set date on update
    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }

}
