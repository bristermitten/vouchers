package me.bristermitten.vouchers.actions;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Action {
    private final ActionType type;
    private final @Nullable String data;

    public Action(ActionType type, @Nullable String data) {
        this.type = type;
        this.data = data;
    }

    public void run(@Nullable Player player) {
        this.run(player, data);
    }

    public void run(@Nullable Player player, @Nullable String data) {
        type.execute(data, player);
    }

    public ActionType getType() {
        return type;
    }

    public @Nullable String getData() {
        return data;
    }


    public String serialize() {
        return String.format("[%s] %s", type.getTag(), data)
                .trim();
    }
}
