package me.bristermitten.vouchers.data.voucher;

import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Singleton
public class VoucherRegistry {
    private final Map<UUID, Voucher> voucherMap = new HashMap<>();

    private final VoucherFactory factory;

    private final Logger logger;

    @Inject
    public VoucherRegistry(VoucherFactory factory, Logger logger) {
        this.factory = factory;
        this.logger = logger;
    }

    public @NotNull Voucher create(VoucherType type, @Nullable String data) {
        Voucher voucher = factory.createVoucher(type, data);
        register(voucher);
        return voucher;
    }

    public Optional<Voucher> get(@NotNull UUID id) {
        return Optional.ofNullable(voucherMap.get(id));
    }

    public ItemStack createVoucherItem(Voucher voucher, @Nullable Player receiver) {
        return factory.createVoucherItem(voucher, voucher.getData(), receiver);
    }

    public void register(@NotNull Voucher voucher) {
        if (voucherMap.put(voucher.getId(), voucher) != null) {
            logger.warning("Voucher with id " + voucher.getId() + " already exists!");
        }
    }
}
