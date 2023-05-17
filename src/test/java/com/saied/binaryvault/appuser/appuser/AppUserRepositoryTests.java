package com.saied.binaryvault.appuser.appuser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.saied.binaryvault.appuser.AbstractTestContainers;
import com.saied.binaryvault.appuser.AppUser;
import com.saied.binaryvault.appuser.AppUserRepository;
import com.saied.binaryvault.appuser.TestConfig;
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
            "password"
        );
        userRepository.save(expected);
        Optional<AppUser> result = userRepository.findByUsername("test");
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    @Test
    void testFindByEmail() {
        AppUser expected = new AppUser(
            1L,
            "test",
            "test@email.com",
            "testFirst",
            "testLast",
            "password"
        );
        userRepository.save(expected);
        Optional<AppUser> result = userRepository.findByEmail("test@email.com");
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
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
            "password"
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
            "password"
        );
        userRepository.save(user);
        result = userRepository.selectExistsEmail("test@email.com");
        assertTrue(result);
    }
}
