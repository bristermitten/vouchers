package me.bristermitten.claimboxes.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.bristermitten.claimboxes.data.persistence.SQLClaimBoxPersistence;
import me.bristermitten.mittenlib.persistence.CachingPersistence;

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
    protected void addToCache(UUID id, ClaimBox data) {
        logger.info("Adding " + id + ", " + data + " to cache");
        final ClaimBox claimBox = lookupAll().get(id);
        if (claimBox != null) {
            logger.info("Found " + id + ", " + claimBox + " in cache");
            // there's already an element in the cache, so merge them
            claimBox.editVoucherIds(voucherIds -> {
                voucherIds.clear();
                voucherIds.addAll(data.getVoucherIds());
            });
            logger.info("Merged " + id + ", " + claimBox + " in cache");
            super.addToCache(id, claimBox);
            return;
        }
        logger.info(id + " wasn't in cache, just adding");
        super.addToCache(id, data);
    }

    public CompletableFuture<ClaimBox> createNewBox(UUID id) {
        final ClaimBox claimBox = new ClaimBox(id, Collections.emptyList());
        return save(claimBox).exceptionally(t -> {
            t.printStackTrace();
            return null;
        }).thenApply(v -> claimBox);
    }

    public CompletableFuture<ClaimBox> getOrCreate(UUID id) {
        return load(id).thenCompose(opt -> opt.map(CompletableFuture::completedFuture).orElseGet(() -> createNewBox(id)));
    }
}
