package me.bristermitten.vouchers.data.voucher;

import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Voucher {
    public static final String NBT_KEY = "MittenVoucher:Voucher";
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

    void setUsed() {
        this.used = true;
    }
}
