package me.bristermitten.vouchers.actions;

import me.bristermitten.vouchers.actions.validate.ValidationResponse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NOOPAction implements ActionType<String> {
    @Override
    public @NotNull String getTag() {
        return "NOOP";
    }

    @Override
    public void execute(@Nullable String value, Player player) {

    }

    @Override
    public ValidationResponse<String> validate(@Nullable String value) {
        return ValidationResponse.ok(value);
    }
}
