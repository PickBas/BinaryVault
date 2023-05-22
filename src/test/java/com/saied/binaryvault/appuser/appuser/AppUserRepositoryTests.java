package com.saied.binaryvault.appuser.appuser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.saied.binaryvault.appuser.AbstractTestContainers;
import com.saied.binaryvault.appuser.AppUser;
import com.saied.binaryvault.appuser.AppUserRepository;
import com.saied.binaryvault.appuser.TestConfig;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
class AppUserRepositoryTests extends AbstractTestContainers {

    @Autowired
    private AppUserRepository userRepository;

    @AfterEach
    void teardown() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByUsername() {
        AppUser expected = new AppUser(
            1L,
            "test",
            "test@email.com",
            "testFirst",
            "testLast",
            "password",
            LocalDateTime.now(),
            LocalDateTime.now(),
            new ArrayList<>()
        );
        userRepository.save(expected);
        Optional<AppUser> result = userRepository.findByUsername("test");
        assertTrue(result.isPresent());
        AppUser resultUser = result.get();
        assertEquals(expected.getUsername(), resultUser.getUsername());
        assertEquals(expected.getEmail(), resultUser.getEmail());
        assertEquals(expected.getFirstName(), resultUser.getFirstName());
        assertEquals(expected.getLastName(), resultUser.getLastName());
    }

    @Test
    void testFindByEmail() {
        AppUser expected = new AppUser(
            1L,
            "test",
            "test@email.com",
            "testFirst",
            "testLast",
            "password",
            LocalDateTime.now(),
            LocalDateTime.now(),
            new ArrayList<>()
        );
        userRepository.save(expected);
        Optional<AppUser> result = userRepository.findByEmail("test@email.com");
        assertTrue(result.isPresent());
        AppUser resultUser = result.get();
        assertEquals(expected.getUsername(), resultUser.getUsername());
        assertEquals(expected.getEmail(), resultUser.getEmail());
        assertEquals(expected.getFirstName(), resultUser.getFirstName());
        assertEquals(expected.getLastName(), resultUser.getLastName());
    }

    @Test
    void testSelectExistsUsername() {
        boolean result = userRepository.selectExistsUsername("test");
        assertFalse(result);
        AppUser user = new AppUser(
            1L,
            "test",
            "test@email.com",
            "testFirst",
            "testLast",
            "password",
            LocalDateTime.now(),
            LocalDateTime.now(),
            new ArrayList<>()
        );
        userRepository.save(user);
        result = userRepository.selectExistsUsername("test");
        assertTrue(result);
    }

    @Test
    void testSelectExistsEmail() {
        boolean result = userRepository.selectExistsEmail("test@email.com");
        assertFalse(result);
        AppUser user = new AppUser(
            1L,
            "test",
            "test@email.com",
            "testFirst",
            "testLast",
            "password",
            LocalDateTime.now(),
            LocalDateTime.now(),
            new ArrayList<>()
        );
        userRepository.save(user);
        result = userRepository.selectExistsEmail("test@email.com");
        assertTrue(result);
    }
}
