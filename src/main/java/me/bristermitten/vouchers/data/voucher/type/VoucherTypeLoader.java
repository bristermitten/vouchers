package me.bristermitten.vouchers.data.voucher.type;

import me.bristermitten.vouchers.actions.Action;
import me.bristermitten.vouchers.actions.ActionParser;
import me.bristermitten.vouchers.data.voucher.ItemDescriptor;
import me.bristermitten.vouchers.data.voucher.VoucherConfig;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VoucherTypeLoader {
    private final ActionParser actionParser;
    private final Logger logger;

    @Inject
    public VoucherTypeLoader(ActionParser actionParser, Logger logger) {
        this.actionParser = actionParser;
        this.logger = logger;
    }

    public Collection<VoucherType> load(VoucherConfig config) {
        return config.voucherTypes().entrySet()
                .stream()
                .map(e -> load(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private VoucherType load(String id, VoucherConfig.VoucherType fromConfig) {
        List<Action> actions = fromConfig.actions().stream()
                .map(msg -> {
                    Optional<Action> opt = actionParser.parse(msg);
                    if (opt.isPresent()) {
                        return opt.get();
                    }
                    logger.warning(() -> "Could not parse action " + msg + " for voucher type " + id);
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String defaultData = fromConfig.defaultValue();

        if (fromConfig.item() == null) {
            return new VoucherCodeType(
                    id, actions, defaultData
            );
        } else {
            return new NormalVoucherType(
                    id, actions, defaultData, loadItemDescriptor(fromConfig.item())
            );
        }
    }

    private ItemDescriptor loadItemDescriptor(VoucherConfig.VoucherType.ItemConfig item) {
        return new ItemDescriptor(
                item.type(),
                item.name(),
                item.lore()
        );
    }
}
