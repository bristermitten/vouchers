package me.bristermitten.vouchers.config;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import me.bristermitten.mittenlib.config.Config;
import me.bristermitten.mittenlib.config.names.NamingPattern;
import me.bristermitten.mittenlib.config.names.NamingPatterns;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Config
@NamingPattern(NamingPatterns.LOWER_KEBAB_CASE)
public interface ItemConfig {
    XMaterial type();
    @Nullable String name();
    @Nullable List<String> lore();

    @Nullable String player();

    default boolean glow() {
        return false;
    }

    @Nullable String dyeColor();

    @Nullable List<ItemFlag> flags();

    @Nullable List<EnchantmentConfig> enchantments();

    default boolean unbreakable() {
        return false;
    }

    @Config
    interface EnchantmentConfig {
        XEnchantment type();
        default int level() {
            return 1;
        }
    }
}
