package me.bristermitten.vouchers.database;

import me.bristermitten.vouchers.config.ClaimBoxesConfig;

public interface JDBCURLFactory {
    String createURL(ClaimBoxesConfig.DatabaseConfig config);
}
