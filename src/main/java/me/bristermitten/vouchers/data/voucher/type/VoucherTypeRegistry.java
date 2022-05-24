package me.bristermitten.vouchers.data.voucher.type;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class VoucherTypeRegistry {
    private final Map<String, VoucherType> voucherTypes = new HashMap<>();
    private final Logger logger = Logger.getLogger(VoucherTypeRegistry.class.getName());


    public void register(@NotNull VoucherType voucherType) {
        if(voucherTypes.put(voucherType.getId(), voucherType) != null) {
            logger.warning(() -> "Duplicate voucher type IDs " + voucherType.getId());
        }
    }

    public Optional<VoucherType> get(String id) {
        return Optional.ofNullable(voucherTypes.get(id));
    }

}
