package me.bristermitten.vouchers.actions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ActionType {
    @NotNull String getTag();

    void execute(@Nullable String value);
}
