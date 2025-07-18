package me.bristermitten.vouchers.config;

import me.bristermitten.mittenlib.config.Config;
import me.bristermitten.mittenlib.config.Source;
import me.bristermitten.mittenlib.config.names.NamingPattern;
import me.bristermitten.mittenlib.config.names.NamingPatterns;
import org.jetbrains.annotations.Nullable;

@Config
@Source("config.yml")
@NamingPattern(NamingPatterns.LOWER_KEBAB_CASE)
public interface ClaimBoxesConfig {
    StorageConfigDTO storage();

    GuiConfigDTO gui();

    ConfirmGUI confirmGui();

    default int updateInterval() {
        return 10;
    }

    @Config
    interface StorageConfigDTO {
        StorageType type();

        @Nullable DatabaseConfig database();

        enum StorageType {
            JSON,
            SQL
        }
    }

    @Config
    interface DatabaseConfig {
        @Nullable String tablePrefix();

        String host();

        int port();

        String username();

        String password();

        String database();
    }

    @Config
    interface GuiConfigDTO {
        String title();

        ItemConfig prevPage();

        ItemConfig nextPage();
    }

    @Config
    interface ConfirmGUI {
        String title();

        ItemConfig confirm();

        ItemConfig cancel();

        ItemConfig background();
    }
}
