package me.bristermitten.vouchers.data.claimbox;

import com.google.inject.Singleton;
import me.bristermitten.vouchers.data.claimbox.persistence.ClaimBoxPersistence;
import me.bristermitten.vouchers.data.claimbox.persistence.SQLClaimBoxPersistence;
import me.bristermitten.vouchers.persist.CachingPersistence;

import javax.inject.Inject;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Singleton
public class ClaimBoxStorage extends CachingPersistence<UUID, ClaimBox> implements ClaimBoxPersistence {
    private final Logger logger = Logger.getLogger(ClaimBoxStorage.class.getName());

    @Inject
    public ClaimBoxStorage(SQLClaimBoxPersistence delegate) {
        super(delegate, ClaimBox::getOwner);
    }

    @Override
    protected ClaimBox addToCache(UUID id, ClaimBox data) {
        ClaimBox previous = cache.getIfPresent(id);
        logger.info(() -> "Adding " + id + ", " + data + " to cache");
        final ClaimBox claimBox = lookupAll().get(id);
        if (claimBox != null) {
            logger.info(() -> "Found " + id + ", " + claimBox + " in cache");
            // there's already an element in the cache, so merge them
            claimBox.editVouchers(voucherIds -> {
                voucherIds.clear();
                voucherIds.addAll(data.getVouchers());
            });
            logger.info(() -> "Merged " + id + ", " + claimBox + " in cache");
            super.addToCache(id, claimBox);
            return previous;
        }
        logger.info(() -> id + " wasn't in cache, just adding");
        super.addToCache(id, data);
        return previous;
    }

    public CompletableFuture<ClaimBox> createNewBox(UUID id) {
        final ClaimBox claimBox = new ClaimBox(id, Collections.emptySet());
        return save(claimBox).exceptionally(t -> {
            t.printStackTrace();
            return null;
        }).thenApply(v -> claimBox);
    }

    public CompletableFuture<ClaimBox> getOrCreate(UUID id) {
        return load(id).thenCompose(opt -> opt.map(CompletableFuture::completedFuture).orElseGet(() -> createNewBox(id)));
    }
}
