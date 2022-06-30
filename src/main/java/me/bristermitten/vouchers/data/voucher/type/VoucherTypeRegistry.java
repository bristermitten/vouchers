package me.bristermitten.vouchers.data.voucher.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;

public interface VoucherTypeRegistry {

    void register(@NotNull VoucherType voucherType);

    Optional<VoucherType> get(String id);

    @Unmodifiable Collection<VoucherType> getAll();
}
