package me.bristermitten.claimboxes.database;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Database.class).to(SQLDatabase.class);
        bind(HikariConfig.class).toProvider(HikariConfigurationProvider.class);
    }
}
