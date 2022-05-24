package me.bristermitten.vouchers.data.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.ClaimBox;
import me.bristermitten.vouchers.database.Database;
import me.bristermitten.vouchers.database.HikariConfigurationProvider;
import me.bristermitten.vouchers.database.JDBCURLFactory;
import me.bristermitten.vouchers.database.SQLDatabase;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLClaimBoxPersistenceTest {

    @Test
    void save() {
        JDBCURLFactory urlFactory = config -> "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        final HikariConfig hikariConfig = new HikariConfigurationProvider(
                () -> new ClaimBoxesConfig.DatabaseConfig("", 0, "", "", ""),
                urlFactory
        ).get();
        final HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        Database database = new SQLDatabase(hikariDataSource);

        final UUID uuid = UUID.fromString("876ce46d-dc56-4a17-9644-0be67fe7c7f6");
        final ClaimBox box = new ClaimBox(uuid, Collections.singletonList("Money 50"));
        final SQLClaimBoxPersistence sqlClaimBoxPersistence = new SQLClaimBoxPersistence(database);
        sqlClaimBoxPersistence.init().join();
        sqlClaimBoxPersistence.delete(uuid).join();
        sqlClaimBoxPersistence.save(box).join();

        Collection<ClaimBox> fromDatabase = sqlClaimBoxPersistence.loadAll().join();
        assertEquals(Collections.singletonList(box), fromDatabase);
    }
}
