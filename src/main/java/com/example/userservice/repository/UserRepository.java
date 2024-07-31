package com.example.userservice.repository;

import com.example.userservice.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u, f FROM User u LEFT JOIN Follow f ON u.id = f.following_id")
    Page<?> findAllWithFollowers(Pageable pageable);

    @Query(value = "SELECT u FROM User u WHERE u.id IN (SELECT f.following_id FROM Follow f WHERE f.follower_id = :userId)")
    List<?> findFollowingByUserId(@Param("userId") UUID userId);
}
