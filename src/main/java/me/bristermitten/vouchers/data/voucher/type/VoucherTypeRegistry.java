package me.bristermitten.vouchers.data.voucher.type;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface VoucherTypeRegistry {

    void register(@NotNull VoucherType voucherType);

    Optional<VoucherType> get(String id);
}
