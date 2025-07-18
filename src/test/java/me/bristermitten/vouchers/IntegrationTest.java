package me.bristermitten.vouchers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class IntegrationTest {
    private static MittenVouchers plugin;

    @Container
    protected static final MariaDBContainer<?> mariaDB = new MariaDBContainer<>("mariadb:latest")
            .withDatabaseName("claimboxes");

    @BeforeAll
    static void setup() {
        MockBukkit.mock();
        plugin = MockBukkit.load(MittenVouchers.class);
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unload();
    }
    @Test
    void integrationTest() {
        PlayerMock playerMock = MockBukkit.getMock().addPlayer();

        assertTrue(playerMock.performCommand(String.format("vouchers give %s money", playerMock.getName())));

        assertNotEquals(-1, playerMock.getInventory().firstEmpty());
    }
}
