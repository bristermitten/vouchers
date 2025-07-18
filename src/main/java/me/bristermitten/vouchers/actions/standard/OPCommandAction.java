package me.bristermitten.vouchers.actions.standard;

import me.bristermitten.vouchers.actions.AbstractActionType;
import me.bristermitten.vouchers.actions.validate.ValidationResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OPCommandAction extends AbstractActionType<String> {
    @Override
    public @NotNull String getTag() {
        return "OP_COMMAND";
    }

    @Override
    public void execute(@Nullable String value, @Nullable Player player) {
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                value
        );
    }

    @Override
    public ValidationResponse<String> validate(@Nullable String value) {
        return requireValuePresent(value);
    }
}
