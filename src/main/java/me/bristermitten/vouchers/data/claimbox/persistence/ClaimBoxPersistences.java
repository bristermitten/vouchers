package me.bristermitten.vouchers.data.claimbox.persistence;

import com.google.inject.Inject;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.mittenlib.util.lambda.Functions;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.claimbox.ClaimBox;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.persist.Persistences;
import org.jetbrains.annotations.NotNull;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClaimBoxPersistences implements Persistences<UUID, ClaimBox, ClaimBoxPersistence>, ClaimBoxPersistence {
    private final Provider<SQLClaimBoxPersistence> sql;
    private final Provider<JSONClaimBoxPersistence> json;
    private final Provider<ClaimBoxesConfig> configProvider;
    private ClaimBoxesConfig.StorageConfigDTO.StorageType previous = null;

    @Inject
    public ClaimBoxPersistences(Provider<SQLClaimBoxPersistence> sql, Provider<JSONClaimBoxPersistence> json, Provider<ClaimBoxesConfig> configProvider) {
        this.sql = sql;
        this.json = json;
        this.configProvider = configProvider;
    }

    @Override
    @NotNull
    public Optional<ClaimBoxPersistence> mariadb() {
        return Optional.of(sql.get());
    }

    private CompletableFuture<ClaimBoxPersistence> getCurrentImplementation() {
        ClaimBoxesConfig.StorageConfigDTO.StorageType type = configProvider.get().storage().type();
        ClaimBoxPersistence persistence;
        switch (type) {
            case SQL:
                persistence = sql.get();
                break;
            case JSON:
                persistence = json.get();
                break;
            default:
                throw new IllegalStateException("Unknown storage type: " + configProvider.get().storage().type());
        }

        boolean typeChanged = previous != null && previous != type;
        previous = type;

        // Always initialize on first use or when type changes
        if (typeChanged || previous == null) {
            return persistence.init().thenApply(Functions.constant(persistence));
        }

        return CompletableFuture.completedFuture(persistence);
    }


    @Override
    public @NotNull CompletableFuture<Unit> removeOne(@NotNull UUID id, Voucher voucher) {
        return getCurrentImplementation().thenCompose(persistence -> persistence.removeOne(id, voucher));
    }

    @Override
    public @NotNull CompletableFuture<Unit> addOne(@NotNull UUID id, Voucher voucherId) {
        return getCurrentImplementation().thenCompose(persistence -> persistence.addOne(id, voucherId));
    }

    @Override
    public @NotNull CompletableFuture<Unit> init() {
        return getCurrentImplementation().thenCompose(ClaimBoxPersistence::init);
    }

    @Override
    public @NotNull CompletableFuture<Unit> flush() {
        return getCurrentImplementation().thenCompose(ClaimBoxPersistence::flush);
    }

    @Override
    public @NotNull CompletableFuture<Optional<ClaimBox>> load(@NotNull UUID id) {
        return getCurrentImplementation().thenCompose(persistence -> persistence.load(id));
    }

    @Override
    public @NotNull CompletableFuture<Unit> delete(@NotNull UUID id) {
        return getCurrentImplementation().thenCompose(persistence -> persistence.delete(id));
    }

    @Override
    public @NotNull CompletableFuture<Collection<ClaimBox>> loadAll() {
        return getCurrentImplementation().thenCompose(ClaimBoxPersistence::loadAll);
    }

    @Override
    public @NotNull CompletableFuture<Unit> saveAll(@NotNull Collection<ClaimBox> values) {
        return getCurrentImplementation().thenCompose(persistence -> persistence.saveAll(values));
    }
}
