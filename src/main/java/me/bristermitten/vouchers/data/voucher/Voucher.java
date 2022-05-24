package me.bristermitten.vouchers.data.voucher;

import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Voucher {
    private final UUID id;
    private final @Nullable String data;

    private final VoucherType type;

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
}
