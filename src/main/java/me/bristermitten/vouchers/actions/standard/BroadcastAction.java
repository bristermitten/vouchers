package me.bristermitten.vouchers.actions.standard;

import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import me.bristermitten.vouchers.actions.ActionType;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class BroadcastAction implements ActionType {
    private final MessageFormatter messageFormatter;
    private final BukkitAudiences bukkitAudiences;

    @Inject
    public BroadcastAction(MessageFormatter messageFormatter, BukkitAudiences bukkitAudiences) {
        this.messageFormatter = messageFormatter;
        this.bukkitAudiences = bukkitAudiences;
    }

    @Override
    public @NotNull String getTag() {
        return "BROADCAST";
    }

    @Override
    public void execute(@Nullable String value, @Nullable Player player) {
        if (value == null) {
            // Nothing to broadcast
            return;
        }
        Component format = messageFormatter.format(value, player);
        bukkitAudiences.players().sendMessage(format);
    }
}
