package me.bristermitten.claimboxes.config;

import me.bristermitten.mittenlib.config.Config;

@Config
public class ClaimBoxesConfigDTO {
    DatabaseConfigDTO databaseConfig;

    @Config
    static class DatabaseConfigDTO {

    }
}
