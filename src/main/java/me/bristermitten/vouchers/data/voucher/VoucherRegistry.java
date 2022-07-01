package me.bristermitten.vouchers.data.voucher;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.bristermitten.vouchers.data.voucher.persistence.VoucherPersistence;
import me.bristermitten.vouchers.data.voucher.persistence.VoucherPersistences;
import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import me.bristermitten.vouchers.persist.CachingPersistence;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Singleton
public class VoucherRegistry extends CachingPersistence<UUID, Voucher> implements VoucherPersistence {
    private final VoucherFactory factory;

    private final Logger logger;

    @Inject
    public VoucherRegistry(VoucherFactory factory, Logger logger, VoucherPersistences persistence) {
        super(persistence, Voucher::getId);
        this.factory = factory;
        this.logger = logger;
    }

    public @NotNull Voucher create(VoucherType type, @Nullable String data) {
        Voucher voucher = factory.createVoucher(type, data);
        register(voucher);
        return voucher;
    }

    public @NotNull CompletableFuture<Voucher> createAndSave(VoucherType type, @Nullable String data) {
        Voucher voucher = create(type, data);
        return save(voucher).thenApply(v -> voucher);
    }


    public @NotNull CompletableFuture<Optional<Voucher>> getFromItem(@NotNull ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        if (Boolean.FALSE.equals(nbtItem.hasKey(Voucher.NBT_KEY))) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        UUID id = UUID.fromString(nbtItem.getString(Voucher.NBT_KEY));
        return load(id);
    }

    public @NotNull Optional<Voucher> lookupFromItem(@NotNull ItemStack item) {
        CompletableFuture<Optional<Voucher>> fromItem = getFromItem(item);
        if (fromItem.isDone()) {
            return fromItem.join();
        }
        return Optional.empty();
    }

    public ItemStack createVoucherItem(Voucher voucher, @Nullable OfflinePlayer receiver) {
        return factory.createVoucherItem(voucher, voucher.getData(), receiver);
    }

    public void register(@NotNull Voucher voucher) {
        if (addToCache(voucher.getId(), voucher) != null) {
            logger.warning("Voucher with id " + voucher.getId() + " already exists!");
        }
        save(voucher);
    }
}
