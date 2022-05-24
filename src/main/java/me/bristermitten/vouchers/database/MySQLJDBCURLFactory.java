package me.bristermitten.vouchers.database;

import me.bristermitten.vouchers.config.ClaimBoxesConfig;

public class MySQLJDBCURLFactory implements JDBCURLFactory {

    @Override
    public String createURL(ClaimBoxesConfig.DatabaseConfig config) {
        return String.format("jdbc:mysql://%s:%d/%s", config.host(), config.port(), config.database());
    }
}
