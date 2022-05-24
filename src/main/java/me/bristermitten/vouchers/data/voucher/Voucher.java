package me.bristermitten.vouchers.data.voucher;

import me.bristermitten.vouchers.actions.Action;
import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Voucher {
    public static final String NBT_KEY = "MittenVoucher:Voucher";
    public static final String DATA_PLACEHOLDER = "{value}";
    private final UUID id;
    private final @Nullable String data;
    private final VoucherType type;

    private boolean used = false;

    public Voucher(UUID id, @Nullable String data, VoucherType type) {
        this.id = id;
        this.data = data;
        this.type = type;
    }

    public VoucherType getType() {
        return type;
    }

    public @Nullable String getData() {
        return data;
    }

    public UUID getId() {
        return id;
    }

    public boolean isUsed() {
        return used;
    }

    public void use(Player user) {
        if (used) {
            throw new IllegalStateException("Voucher has already been used!");
        }
        this.used = true;
        for (Action action : type.getActions()) {
            String actionData = action.getData();
            if (actionData != null && this.data != null) {
                actionData = actionData.replace(Voucher.DATA_PLACEHOLDER, this.data);
            }
            action.run(user, actionData);
        }
    }
}
