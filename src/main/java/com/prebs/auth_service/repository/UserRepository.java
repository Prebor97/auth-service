package com.prebs.auth_service.repository;

import com.prebs.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE " +
            "(:name IS NULL OR u.name = :name) AND " +
            "(:email IS NULL OR u.email = :email) AND " +
            "(:isActivated IS NULL OR u.isActivated = :isActivated)")
    List<User> findByFilters(
            @Param("name") String name,
            @Param("email") String email,
            @Param("isActivated") Boolean is_activated);
}
