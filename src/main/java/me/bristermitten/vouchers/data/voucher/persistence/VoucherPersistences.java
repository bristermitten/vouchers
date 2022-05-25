package me.bristermitten.vouchers.data.voucher.persistence;

import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.voucher.Voucher;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VoucherPersistences implements VoucherPersistence {
    private final SQLVoucherPersistence sqlVoucherPersistence;
    private final Provider<ClaimBoxesConfig> configProvider;

    @Inject
    public VoucherPersistences(SQLVoucherPersistence sqlVoucherPersistence, Provider<ClaimBoxesConfig> configProvider) {
        this.sqlVoucherPersistence = sqlVoucherPersistence;
        this.configProvider = configProvider;
    }

    private VoucherPersistence getCurrentImplementation() {
        switch (configProvider.get().storage().type()) {
            case SQL:
                return sqlVoucherPersistence;

            default:
                throw new IllegalStateException("Unknown storage type: " + configProvider.get().storage().type());
        }
    }

    @Override
    public @NotNull CompletableFuture<Unit> init() {
        return getCurrentImplementation().init();
    }

    @Override
    public @NotNull CompletableFuture<Unit> cleanup() {
        return getCurrentImplementation().cleanup();
    }

    @Override
    public @NotNull CompletableFuture<Unit> save(@NotNull Voucher value) {
        return getCurrentImplementation().save(value);
    }

    @Override
    public @NotNull CompletableFuture<Optional<Voucher>> load(@NotNull UUID id) {
        return getCurrentImplementation().load(id);
    }

    @Override
    public @NotNull CompletableFuture<Unit> delete(@NotNull UUID id) {
        return getCurrentImplementation().delete(id);
    }

    @Override
    public @NotNull CompletableFuture<Collection<Voucher>> loadAll() {
        return getCurrentImplementation().loadAll();
    }

    @Override
    public @NotNull CompletableFuture<Unit> saveAll(@NotNull Collection<Voucher> values) {
        return getCurrentImplementation().saveAll(values);
    }
}
