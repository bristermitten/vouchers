package me.bristermitten.claimboxes.database;

import me.bristermitten.claimboxes.config.ClaimBoxesConfig;

public class MySQLJDBCURLFactory implements JDBCURLFactory {

    @Override
    public String createURL(ClaimBoxesConfig.DatabaseConfig config) {
        return String.format("jdbc:mysql://%s:%d/%s", config.host(), config.port(), config.database());
    }
}
