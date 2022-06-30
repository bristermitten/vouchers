package me.bristermitten.vouchers.data.voucher;

import me.bristermitten.vouchers.actions.Action;
import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Voucher {
    public static final String NBT_KEY = "MittenVoucher:Voucher";
    public static final String DATA_PLACEHOLDER = "{value}";
    public static final String PLAYER_PLACEHOLDER = "{player}";
    private final UUID id;
    private final @Nullable String data;
    private final VoucherType type;

    private boolean used;

    public Voucher(UUID id, @Nullable String data, VoucherType type) {
        this(id, data, type, false);
    }

    public Voucher(UUID id, @Nullable String data, VoucherType type, boolean used) {
        this.id = id;
        this.data = data;
        this.type = type;
        this.used = used;
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

    public void use(@NotNull Player user) throws VoucherUsageException {
        if (used) {
            throw new IllegalStateException("Voucher has already been used!");
        }
        Optional<String> permission = type.getPermission();
        if (permission.isPresent() && !user.hasPermission(permission.get())) {
            throw new VoucherUsageException.NoPermission(permission.get());
        }
        this.used = true;
        for (Action action : type.getActions()) {
            String actionData = action.getData();
            if (actionData != null && this.data != null) {
                actionData = actionData.replace(Voucher.DATA_PLACEHOLDER, this.data);
                actionData = actionData.replace(PLAYER_PLACEHOLDER, user.getName());
            }
            action.run(user, actionData);
        }
    }
}
