package me.bristermitten.vouchers.actions;

import me.bristermitten.vouchers.actions.validate.ValidationResponse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Action {
    public static final String DATA_PLACEHOLDER = "{value}";
    public static final String PLAYER_PLACEHOLDER = "{player}";
    private final ActionType<?> type;
    private final @Nullable String argument;

    public Action(ActionType<?> type, @Nullable String argument) {
        this.type = type;
        this.argument = argument;
    }


    public void runWith(@Nullable Player player, @Nullable String data) {
        if (this.argument == null) {
            type.execute(data, player);
            return;
        }
        String finalArgument = this.argument;
        if (player != null) {
            finalArgument = finalArgument.replace(PLAYER_PLACEHOLDER, player.getName());
        }
        if (data != null) {
            finalArgument = finalArgument.replace(DATA_PLACEHOLDER, data);
        }
        type.execute(finalArgument, player);
    }

    public ValidationResponse<?> validate() {
        return type.validate(this.argument);
    }

    public ValidationResponse<?> validateWith(@Nullable String data, @Nullable Player player) {
        if (this.argument == null) {
            return type.validate(data);
        }
        String finalArgument = this.argument;
        if (player != null) {
            finalArgument = finalArgument.replace(PLAYER_PLACEHOLDER, player.getName());
        }
        if (data != null) {
            finalArgument = finalArgument.replace(DATA_PLACEHOLDER, data);
        }
        return type.validate(finalArgument);
    }

    public ActionType<?> getType() {
        return type;
    }

    public @Nullable String getArgument() {
        return argument;
    }


    public String serialize() {
        return String.format("[%s] %s", type.getTag(), argument)
                .trim();
    }
}
