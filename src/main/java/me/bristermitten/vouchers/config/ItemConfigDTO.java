package me.bristermitten.vouchers.config;

import com.cryptomorin.xseries.XMaterial;
import me.bristermitten.mittenlib.config.Config;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Config
public
class ItemConfigDTO {
    XMaterial type;
    @Nullable String name;
    @Nullable List<String> lore;

    @Nullable String player;
}
