package me.bristermitten.vouchers.data.claimbox.persistence;

import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.data.claimbox.ClaimBox;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.persist.Persistence;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClaimBoxPersistence extends Persistence<UUID, ClaimBox> {

    @NotNull CompletableFuture<Unit> removeOne(@NotNull UUID id, Voucher voucher);

    @NotNull CompletableFuture<Unit> addOne(@NotNull UUID id, Voucher voucherId);
}
