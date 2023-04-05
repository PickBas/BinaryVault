package com.saied.binaryvault.appuser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("SELECT s FROM app_user s WHERE s.username = ?1")
    Optional<AppUser> findByUsername(String username);
    @Query("SELECT s FROM app_user s WHERE s.email = ?1")
    Optional<AppUser> findByEmail(String email);
}
