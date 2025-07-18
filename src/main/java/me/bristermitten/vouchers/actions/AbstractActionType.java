package me.bristermitten.vouchers.actions;

import me.bristermitten.vouchers.actions.validate.ValidationResponse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractActionType<T> implements ActionType<T> {

    public @NotNull ValidationResponse<@NotNull String> requireValuePresent(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return ValidationResponse.error("Value must be present for action type: " + getTag());
        } else {
            return ValidationResponse.ok(value);
        }

    }


    public @NotNull Player requirePlayerPresent(@Nullable Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player must be present for action type: " + getTag());
        }
        return player;
    }

    public ValidationResponse<Double> isValidDouble(@NotNull String value) {
        try {
            double amount = Double.parseDouble(value);
            return ValidationResponse.ok(amount);
        } catch (NumberFormatException e) {
            return ValidationResponse.error("Value must be a valid double for action type: " + getTag());
        }
    }

    public ValidationResponse<Double> isPositive(@NotNull Double value) {
        if (value <= 0) {
            return ValidationResponse.error("Value must be greater than zero for action type: " + getTag());
        }
        return ValidationResponse.ok(value);
    }
}
