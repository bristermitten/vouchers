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
public class ItemConfigDTO {
    XMaterial type;
    @Nullable String name;
    @Nullable List<String> lore;

    @Nullable String player;

    boolean glow = false;

    @Nullable String dyeColor;

    @Nullable List<ItemFlag> flags;

    @Nullable List<EnchantmentDTO> enchantments;

    boolean unbreakable = false;

    @Config
    static class EnchantmentDTO {
        XEnchantment type;
        int level = 1;
    }
}
