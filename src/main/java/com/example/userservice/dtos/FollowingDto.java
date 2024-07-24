package com.example.userservice.dtos;

import java.util.UUID;

public class FollowingDto {
    private UUID follower_id;
    private UUID following_id;

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
