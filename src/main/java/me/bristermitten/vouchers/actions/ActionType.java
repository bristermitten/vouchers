package me.bristermitten.vouchers.actions;

import me.bristermitten.vouchers.actions.validate.ValidationResponse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ActionType<T> {
    @NotNull String getTag();

    void execute(@Nullable String value, @Nullable Player player);

    ValidationResponse<T> validate(@Nullable String value);
}
