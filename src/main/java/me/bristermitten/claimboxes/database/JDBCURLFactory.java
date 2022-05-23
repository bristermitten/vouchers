package me.bristermitten.claimboxes.database;

import me.bristermitten.claimboxes.config.ClaimBoxesConfig;

public interface JDBCURLFactory {
    String createURL(ClaimBoxesConfig.DatabaseConfig config);
}
