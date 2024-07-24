package com.example.userservice.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Follows")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private UUID follower_id;

    @Column
    private UUID following_id;

    public Follow () {}
    //setter and getters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(UUID follower_id) {
        this.follower_id = follower_id;
    }

    public UUID getFollowing_id() {
        return following_id;
    }

    public void setFollowing_id(UUID following_id) {
        this.following_id = following_id;
    }
}
