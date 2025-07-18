package me.bristermitten.vouchers.data.voucher.persistence;

import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.mittenlib.util.lambda.Functions;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.persist.Persistence;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VoucherPersistences implements VoucherPersistence {
    private final Provider<SQLVoucherPersistence> sqlVoucherPersistence;
    private final JSONVoucherPersistence jsonVoucherPersistence;
    private final Provider<ClaimBoxesConfig> configProvider;

    private ClaimBoxesConfig.StorageConfigDTO.StorageType previous = null;

    @Inject
    public VoucherPersistences(Provider<SQLVoucherPersistence> sqlVoucherPersistence, JSONVoucherPersistence jsonVoucherPersistence, Provider<ClaimBoxesConfig> configProvider) {
        this.sqlVoucherPersistence = sqlVoucherPersistence;
        this.jsonVoucherPersistence = jsonVoucherPersistence;
        this.configProvider = configProvider;
    }

    private CompletableFuture<VoucherPersistence> getCurrentImplementation() {
        ClaimBoxesConfig.StorageConfigDTO.StorageType type = configProvider.get().storage().type();
        VoucherPersistence persistence;
        switch (type) {
            case SQL:
                persistence = sqlVoucherPersistence.get();
                break;
            case JSON:
                persistence = jsonVoucherPersistence;
                break;
            default:
                throw new IllegalStateException("Unknown storage type: " + configProvider.get().storage().type());
        }
        if (previous != null && previous != type) {
            previous = type;
            return persistence.init().thenApply(Functions.constant(persistence)); // Set up the new persistence
        }
        previous = type;
        return CompletableFuture.completedFuture(persistence);
    }


    @Override
    public @NotNull CompletableFuture<Unit> init() {
        return getCurrentImplementation().thenCompose(VoucherPersistence::init);
    }

    @Override
    public @NotNull CompletableFuture<Unit> cleanup() {
        return getCurrentImplementation().thenCompose(VoucherPersistence::cleanup);
    }

    @Override
    public @NotNull CompletableFuture<Unit> flush() {
        return getCurrentImplementation().thenCompose(VoucherPersistence::flush);
    }


    @Override
    public @NotNull CompletableFuture<Unit> save(@NotNull Voucher value) {
        return getCurrentImplementation().thenCompose(persistence -> persistence.save(value));
    }

    @Override
    public @NotNull CompletableFuture<Optional<Voucher>> load(@NotNull UUID id) {
        return getCurrentImplementation().thenCompose(persistence -> persistence.load(id));
    }

    @Override
    public @NotNull CompletableFuture<Unit> delete(@NotNull UUID id) {
        return getCurrentImplementation().thenCompose(persistence -> persistence.delete(id));
    }

    @Override
    public @NotNull CompletableFuture<Collection<Voucher>> loadAll() {
        return getCurrentImplementation().thenCompose(Persistence::loadAll);
    }

    @Override
    public @NotNull CompletableFuture<Unit> saveAll(@NotNull Collection<Voucher> values) {
        return getCurrentImplementation().thenCompose(persistence -> persistence.saveAll(values));
    }
}
