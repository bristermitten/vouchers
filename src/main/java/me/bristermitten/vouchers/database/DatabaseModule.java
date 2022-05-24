package me.bristermitten.vouchers.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;

import javax.inject.Provider;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Database.class).to(SQLDatabase.class);
        bind(HikariConfig.class).toProvider(HikariConfigurationProvider.class);
        bind(JDBCURLFactory.class).to(MySQLJDBCURLFactory.class);
    }

    @Provides
    public ClaimBoxesConfig.DatabaseConfig getDatabaseConfig(Provider<ClaimBoxesConfig> configProvider) {
        return configProvider.get().database();
    }

    @Provides
    public HikariDataSource getDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }
}
