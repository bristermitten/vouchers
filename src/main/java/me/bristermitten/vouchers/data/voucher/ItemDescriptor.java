package me.bristermitten.vouchers.data.voucher;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDescriptor {
    private final @NotNull Material type;
    private final @Nullable String name;
    private final @Nullable List<String> lore;

    public ItemDescriptor(@NotNull Material type, @Nullable String name, @Nullable List<String> lore) {
        this.type = type;
        this.name = name;
        this.lore = lore;
    }

    public @NotNull Material getType() {
        return type;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable List<String> getLore() {
        return lore;
    }
}
