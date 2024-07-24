package com.example.userservice.repository;

import com.example.userservice.models.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {
    @Query(value = "select f.* from follows f where f.following_id = :following and f.follower_id = :follower", nativeQuery = true)
    Follow findFollowingByFollower(@Param("following") UUID following, @Param("follower") UUID follower);
}
