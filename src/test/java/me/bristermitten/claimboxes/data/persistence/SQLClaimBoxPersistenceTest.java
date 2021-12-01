package me.bristermitten.claimboxes.data.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.bristermitten.claimboxes.config.DatabaseConfig;
import me.bristermitten.claimboxes.data.ClaimBox;
import me.bristermitten.claimboxes.database.Database;
import me.bristermitten.claimboxes.database.HikariConfigurationProvider;
import me.bristermitten.claimboxes.database.SQLDatabase;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

class SQLClaimBoxPersistenceTest {

    @Test
    void save() {
        final HikariConfig hikariConfig = new HikariConfigurationProvider(() -> new DatabaseConfig("194.163.132.49", 3306, "u3_Bh7cOhECEd", "@ekNPyC^j8!GFG02dA9OR@=H", "s3_main")).get();
        final HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        Database database = new SQLDatabase(hikariDataSource);

        final UUID uuid = UUID.fromString("876ce46d-dc56-4a17-9644-0be67fe7c7f6");
        final ClaimBox box = new ClaimBox(uuid, Collections.singletonList("Money 50"));
        final SQLClaimBoxPersistence sqlClaimBoxPersistence = new SQLClaimBoxPersistence(database);
        sqlClaimBoxPersistence.delete(uuid).join();
        sqlClaimBoxPersistence.save(box).join();

        System.out.println(sqlClaimBoxPersistence.loadAll().join());
    }
}
