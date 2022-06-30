package me.bristermitten.vouchers.data.voucher.type;

import me.bristermitten.vouchers.config.VoucherConfig;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Stores voucher types
 * To allow live reloading, voucher types from the config are not stored in the main cache.
 * Instead, we rely on MittenLib's config caching mechanisms to store them, turning into a VoucherType when
 * needed.
 *
 * Manually registered types with {@link #register(VoucherType)} <i>are</i> cached,
 * and won't be reloaded automatically
 */
@Singleton
public class VoucherTypeCache implements VoucherTypeRegistry {
    private final Map<String, VoucherType> voucherTypes = new HashMap<>();
    private final Logger logger = Logger.getLogger(VoucherTypeCache.class.getName());

    private final Provider<VoucherConfig> configProvider;

    private final VoucherTypeLoader loader;

    @Inject
    public VoucherTypeCache(Provider<VoucherConfig> configProvider, VoucherTypeLoader loader) {
        this.configProvider = configProvider;
        this.loader = loader;
    }


    @Override
    public void register(@NotNull VoucherType voucherType) {
        if (voucherTypes.put(voucherType.getId(), voucherType) != null) {
            logger.warning(() -> "Duplicate voucher type IDs " + voucherType.getId());
        }
    }

    @Override
    public Optional<VoucherType> get(String id) {
        if (voucherTypes.containsKey(id)) {
            return Optional.of(voucherTypes.get(id));
        }
        return Optional.ofNullable(configProvider.get().voucherTypes().get(id))
                .map(config -> loader.load(id, config));
    }
}
