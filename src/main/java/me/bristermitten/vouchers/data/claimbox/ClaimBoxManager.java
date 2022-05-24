package me.bristermitten.vouchers.data.claimbox;

import com.google.inject.Inject;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.VoucherUtil;
import me.bristermitten.vouchers.data.claimbox.persistence.SQLClaimBoxPersistence;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClaimBoxManager {
    private final ClaimBoxStorage claimBoxStorage;
    private final SQLClaimBoxPersistence persistence;

    @Inject
    public ClaimBoxManager(ClaimBoxStorage claimBoxStorage, SQLClaimBoxPersistence persistence) {
        this.claimBoxStorage = claimBoxStorage;
        this.persistence = persistence;
    }

    public CompletableFuture<Unit> reset(ClaimBox claimBox) {
        claimBox.editVoucherIds(List::clear);
        return persistence.delete(claimBox.getOwner())
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    public CompletableFuture<ClaimBox> getBox(UUID owner) {
        return claimBoxStorage.getOrCreate(owner);
    }

    public CompletableFuture<Unit> give(ClaimBox claimBox, String voucherId, @Nullable String arg) {
        final String voucherString = VoucherUtil.makeVoucherString(voucherId, arg);
        claimBox.editVoucherIds(v -> v.add(voucherString));
        return persistence.addOne(claimBox.getOwner(), voucherString)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }


    public void remove(ClaimBox claimBox, String voucherId, @Nullable String arg) {
        final String voucherString = VoucherUtil.makeVoucherString(voucherId, arg);
        claimBox.editVoucherIds(v -> v.remove(voucherString));
        persistence.removeOne(claimBox.getOwner(), voucherString)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    public CompletableFuture<Set<UUID>> giveAll(String group, boolean online, String voucherId, @Nullable String arg) {
        throw new UnsupportedOperationException("TODO");
    }

    public CompletableFuture<Void> resetAll() {
        return claimBoxStorage.loadAll()
                .thenRun(() -> {
                    for (ClaimBox value : claimBoxStorage.lookupAll().values()) {
                        reset(value);
                    }
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
