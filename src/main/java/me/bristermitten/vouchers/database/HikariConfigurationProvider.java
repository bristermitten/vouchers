package me.bristermitten.vouchers.database;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import org.jetbrains.annotations.NotNull;

import javax.inject.Provider;

public class HikariConfigurationProvider implements Provider<HikariConfig> {
    private final Provider<ClaimBoxesConfig.DatabaseConfig> configurationProvider;
    private final JDBCURLFactory urlFactory;

    @Inject
    public HikariConfigurationProvider(Provider<ClaimBoxesConfig.DatabaseConfig> configurationProvider, JDBCURLFactory urlFactory) {
        this.configurationProvider = configurationProvider;
        this.urlFactory = urlFactory;
    }


    @Override
    @NotNull
    public HikariConfig get() {
        ClaimBoxesConfig.DatabaseConfig config = configurationProvider.get();
        final HikariConfig hikariConfig = new HikariConfig();
        final String jdbcURL = urlFactory.createURL(config);
        hikariConfig.setJdbcUrl(jdbcURL);
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        return hikariConfig;
    }
}
