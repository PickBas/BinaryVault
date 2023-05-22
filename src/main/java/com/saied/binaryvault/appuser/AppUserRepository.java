package com.saied.binaryvault.appuser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("SELECT user FROM app_user user WHERE user.username = ?1")
    Optional<AppUser> findByUsername(String username);

    @Query("SELECT user FROM app_user user WHERE user.email = ?1")
    Optional<AppUser> findByEmail(String email);

    @Query(
        """
        SELECT
            CASE WHEN COUNT(user) > 0
                THEN TRUE
                ELSE FALSE
            END
        FROM app_user user WHERE user.email = ?1
        """
    )
    Boolean selectExistsEmail(String email);

    @Query(
        """
        SELECT
            CASE WHEN COUNT(user) > 0
                THEN TRUE
                ELSE FALSE
            END
        FROM app_user user WHERE user.username = ?1
        """
    )
    Boolean selectExistsUsername(String username);
}
