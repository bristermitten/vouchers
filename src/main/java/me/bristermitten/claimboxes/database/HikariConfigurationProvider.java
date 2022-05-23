package me.bristermitten.claimboxes.database;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import me.bristermitten.claimboxes.config.ClaimBoxesConfig;
import org.jetbrains.annotations.NotNull;

import javax.inject.Provider;

public class HikariConfigurationProvider implements Provider<HikariConfig> {
    private final Provider<ClaimBoxesConfig.DatabaseConfig> configurationProvider;

    @Inject
    public HikariConfigurationProvider(Provider<ClaimBoxesConfig.DatabaseConfig> configurationProvider) {
        this.configurationProvider = configurationProvider;
    }


    @Override
    @NotNull
    public HikariConfig get() {
        ClaimBoxesConfig.DatabaseConfig config = configurationProvider.get();
        final HikariConfig hikariConfig = new HikariConfig();
        final String jdbcURL = String.format("jdbc:mysql://%s:%d/%s", config.host(), config.port(), config.database());
        hikariConfig.setJdbcUrl(jdbcURL);
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        return hikariConfig;
    }
}
