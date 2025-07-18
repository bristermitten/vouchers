package me.bristermitten.vouchers.data.claimbox;

import com.google.inject.Singleton;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.data.claimbox.persistence.ClaimBoxPersistence;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.persist.CachingPersistence;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Singleton
public class ClaimBoxStorage extends CachingPersistence<UUID, ClaimBox, ClaimBoxPersistence> implements ClaimBoxPersistence {
    private final Logger logger = Logger.getLogger(ClaimBoxStorage.class.getName());

    @Inject
    public ClaimBoxStorage(ClaimBoxPersistence delegate) {
        super(delegate, ClaimBox::getOwner);
    }

    @Override
    protected ClaimBox addToCache(UUID id, ClaimBox data) {
        logger.info(() -> "Adding " + id + ", " + data + " to cache");
        final ClaimBox claimBox = cache.getIfPresent(id);
        if (claimBox != null && !data.equals(claimBox)) {
            logger.info(() -> "Found " + id + ", " + claimBox + " in cache");
            // there's already an element in the cache, so merge them
            claimBox.editVouchers(voucherIds -> {
                voucherIds.clear();
                voucherIds.addAll(data.getVouchers());
            });
            logger.info(() -> "Merged " + id + ", " + claimBox + " in cache");
            super.addToCache(id, claimBox);
            return claimBox;
        }
        logger.info(() -> id + " wasn't in cache, just adding");
        super.addToCache(id, data);
        return data;
    }

    public CompletableFuture<ClaimBox> createNewBox(UUID id) {
        final ClaimBox claimBox = new ClaimBox(id, Collections.emptySet());
        return save(claimBox).thenApply(v -> claimBox);
    }

    public CompletableFuture<ClaimBox> getOrCreate(UUID id) {
        return load(id).thenCompose(opt -> opt.map(CompletableFuture::completedFuture).orElseGet(() -> createNewBox(id)));
    }

    @Override
    public @NotNull CompletableFuture<Unit> removeOne(@NotNull UUID id, Voucher voucher) {
        return delegate.removeOne(id, voucher);
    }

    @Override
    public @NotNull CompletableFuture<Unit> addOne(@NotNull UUID id, Voucher voucherId) {
        return delegate.addOne(id, voucherId);
    }
}
