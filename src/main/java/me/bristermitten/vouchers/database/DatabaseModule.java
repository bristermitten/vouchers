package me.bristermitten.vouchers.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.bristermitten.mittenlib.config.provider.DelegatingConfigProvider;
import me.bristermitten.mittenlib.files.json.ExtraTypeAdapter;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.voucher.persistence.VoucherTypeTypeAdapter;
import org.jetbrains.annotations.Nullable;

import javax.inject.Provider;
import javax.inject.Singleton;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Database.class).to(SQLDatabase.class);
        bind(HikariConfig.class).toProvider(HikariConfigurationProvider.class);
        bind(JDBCURLFactory.class).to(MySQLJDBCURLFactory.class);
        Multibinder.newSetBinder(binder(), new TypeLiteral<ExtraTypeAdapter<?>>() {
        }).addBinding().to(VoucherTypeTypeAdapter.class);
    }

    @Provides
    public @Nullable ClaimBoxesConfig.DatabaseConfig getDatabaseConfig(Provider<ClaimBoxesConfig> configProvider) {
        return configProvider.get().storage().database();
    }

    @Provides
    @Singleton
    public HikariDataSource getDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }
}
