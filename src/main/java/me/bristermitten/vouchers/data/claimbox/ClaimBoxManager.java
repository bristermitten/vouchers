package me.bristermitten.vouchers.data.claimbox;

import me.bristermitten.mittenlib.util.Futures;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.data.voucher.Voucher;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Manages ClaimBox entities and provides operations for manipulating them.
 * This class serves as the primary interface for ClaimBox operations.
 */
public class ClaimBoxManager {
    private final ClaimBoxStorage storage;

    @Inject
    public ClaimBoxManager(ClaimBoxStorage storage) {
        this.storage = storage;
    }

    public CompletableFuture<Unit> reset(ClaimBox claimBox) {
        claimBox.editVouchers(Set::clear);
        return storage.delete(claimBox.getOwner());
    }

    public CompletableFuture<ClaimBox> getBox(UUID owner) {
        return storage.getOrCreate(owner);
    }

    public CompletableFuture<Unit> give(ClaimBox claimBox, Voucher voucher) {
        claimBox.editVouchers(v -> v.add(voucher));
        return storage.addOne(claimBox.getOwner(), voucher);
    }

    public CompletableFuture<Unit> remove(ClaimBox claimBox, Voucher voucher) {
        claimBox.editVouchers(v -> v.remove(voucher));
        return storage.removeOne(claimBox.getOwner(), voucher);
    }

    public CompletableFuture<Set<UUID>> giveAll(String group, boolean online, String voucherId, @Nullable String arg) {
        throw new UnsupportedOperationException("TODO");
    }

    public CompletableFuture<Unit> resetAll() {
        return storage.loadAll()
                .thenCompose(claimBoxes -> {
                    List<CompletableFuture<Unit>> resetFutures = claimBoxes.stream()
                            .map(this::reset)
                            .collect(Collectors.toList());
                    return Futures.sequence(resetFutures).thenApply(results -> Unit.UNIT);
                });
    }
}
