package com.saied.binaryvault;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestContainersTest extends AbstractTestContainers {

    @Test
    void canStartPostgresDB() {
        assertTrue(psqlContainer.isRunning());
        assertTrue(psqlContainer.isCreated());
    }

}
