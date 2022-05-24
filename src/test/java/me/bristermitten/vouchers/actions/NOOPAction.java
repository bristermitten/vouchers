package me.bristermitten.vouchers.actions;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NOOPAction implements ActionType {
    @Override
    public @NotNull String getTag() {
        return "NOOP";
    }

    @Override
    public void execute(@Nullable String value, Player player) {

    }
}
