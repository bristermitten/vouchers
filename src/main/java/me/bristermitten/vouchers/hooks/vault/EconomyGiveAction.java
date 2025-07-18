package me.bristermitten.vouchers.hooks.vault;

import me.bristermitten.vouchers.actions.AbstractActionType;
import me.bristermitten.vouchers.actions.validate.ValidationResponse;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class EconomyGiveAction extends AbstractActionType<Double> {

    private final Economy economy;

    @Inject
    public EconomyGiveAction(Economy economy) {
        this.economy = economy;
    }

    @Override
    @NotNull
    public String getTag() {
        return "ECO_GIVE";
    }

    @Override
    public void execute(@Nullable String value, @Nullable Player player) {
        double amount = validate(value).getOrThrow();

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero for action type: " + getTag());
        }

        economy.depositPlayer(player, amount);
    }

    @Override
    public ValidationResponse<Double> validate(@Nullable String value) {
        return requireValuePresent(value)
                .then(this::isValidDouble)
                .then(this::isPositive);
    }
}
