package me.bristermitten.vouchers.util;

import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public class Formatting {
    private Formatting() {
    }

    public static String legacyFullyFormat(MessageFormatter formatter, String text, @Nullable OfflinePlayer player) {
        Component format = formatter.format(text, player);
        return LegacyComponentSerializer.legacySection().serialize(format);
    }
}
