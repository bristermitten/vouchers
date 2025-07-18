package me.bristermitten.vouchers.data.persistence;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.inject.Injector;
import com.zaxxer.hikari.HikariConfig;
import me.bristermitten.mittenlib.MittenLib;
import me.bristermitten.vouchers.actions.ActionModule;
import me.bristermitten.vouchers.config.ClaimBoxesConfigImpl;
import me.bristermitten.vouchers.config.VoucherConfig;
import me.bristermitten.vouchers.data.claimbox.ClaimBox;
import me.bristermitten.vouchers.data.claimbox.persistence.SQLClaimBoxPersistence;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.data.voucher.VoucherModule;
import me.bristermitten.vouchers.data.voucher.VoucherRegistry;
import me.bristermitten.vouchers.data.voucher.persistence.SQLVoucherPersistence;
import me.bristermitten.vouchers.data.voucher.persistence.VoucherPersistence;
import me.bristermitten.vouchers.data.voucher.type.VoucherCodeType;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeRegistry;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeSettings;
import me.bristermitten.vouchers.database.DatabaseModule;
import me.bristermitten.vouchers.database.HikariConfigurationProvider;
import me.bristermitten.vouchers.database.JDBCURLFactory;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
class SQLClaimBoxPersistenceTest {

    @Container
    protected static final MariaDBContainer<?> mariaDB = new MariaDBContainer<>("mariadb:latest")
            .withDatabaseName("testdb");
    private JavaPlugin plugin;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unload();
    }

    @Test
    void save() {
        JDBCURLFactory urlFactory = config -> mariaDB.getJdbcUrl();
        final HikariConfig hikariConfig = new HikariConfigurationProvider(
                () -> new ClaimBoxesConfigImpl.DatabaseConfigImpl("", "", 0, mariaDB.getUsername(), mariaDB.getPassword(), ""),
                urlFactory
        ).get();
        final Injector injector = MittenLib.withDefaults(plugin)
                .addConfigModules(ClaimBoxesConfigImpl.CONFIG, VoucherConfig.CONFIG)
                .addModules(new VoucherModule(), new ActionModule())
                .addModules(new DatabaseModule())
                .addModules(new DatabaseModule() {
                    @Override
                    protected void configure() {
                        bind(HikariConfig.class).toInstance(hikariConfig);
                    }
                })
                .build();

        VoucherCodeType voucherCodeType = new VoucherCodeType("test",
                new VoucherTypeSettings(new ArrayList<>(), null, null,
                        null, null, null));

        Voucher voucher = injector.getInstance(VoucherRegistry.class)
                .create(voucherCodeType, null);
        final UUID uuid = UUID.fromString("876ce46d-dc56-4a17-9644-0be67fe7c7f6");

        final ClaimBox box = new ClaimBox(uuid, Collections.singleton(voucher));


        injector.getInstance(VoucherTypeRegistry.class).register(voucherCodeType);
        final VoucherPersistence voucherPersistence = injector.getInstance(SQLVoucherPersistence.class);
        final SQLClaimBoxPersistence sqlClaimBoxPersistence = injector.getInstance(SQLClaimBoxPersistence.class);

        voucherPersistence.init().join();
        sqlClaimBoxPersistence.init().join();
        voucherPersistence.save(voucher).join();
        sqlClaimBoxPersistence.save(box).join();

        Collection<ClaimBox> fromDatabase = sqlClaimBoxPersistence.loadAll().join();
        assertEquals(Collections.singletonList(box), fromDatabase);
    }
}
