package me.bristermitten.vouchers.config;

import me.bristermitten.mittenlib.config.Config;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Config
public
class ItemConfigDTO {
    Material type;
    @Nullable String name;
    @Nullable List<String> lore;

    short data = 0;

    @Nullable String player;
}
