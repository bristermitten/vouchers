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

    private void loadFromConfig() {
        this.voucherTypes.clear();
        loader.load(configProvider.get())
                .forEach(this::register);
    }

    @Override
    public void register(@NotNull VoucherType voucherType) {
        if (voucherTypes.put(voucherType.getId(), voucherType) != null) {
            logger.warning(() -> "Duplicate voucher type IDs " + voucherType.getId());
        }
    }

    @Override
    public Optional<VoucherType> get(String id) {
        VoucherType value = voucherTypes.get(id);
        if (value == null) {
            loadFromConfig(); // Try and reload the config, in case it was changed
            value = voucherTypes.get(id);
        }
        return Optional.ofNullable(value);
    }
}
