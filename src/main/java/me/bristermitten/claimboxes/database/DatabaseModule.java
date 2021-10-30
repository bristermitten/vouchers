package me.bristermitten.claimboxes.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.bristermitten.claimboxes.config.ClaimBoxesConfig;
import me.bristermitten.claimboxes.config.DatabaseConfig;

import javax.inject.Provider;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Database.class).to(SQLDatabase.class);
        bind(HikariConfig.class).toProvider(HikariConfigurationProvider.class);
    }

    @Provides
    public DatabaseConfig getDatabaseConfig(Provider<ClaimBoxesConfig> configProvider) {
        return configProvider.get().database();
    }

    @Provides
    public HikariDataSource getDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }
}
