package me.bristermitten.vouchers.data.voucher.type;

import me.bristermitten.mittenlib.lang.LangMessage;
import me.bristermitten.vouchers.actions.Action;
import me.bristermitten.vouchers.actions.ActionParser;
import me.bristermitten.vouchers.config.ItemConfig;
import me.bristermitten.vouchers.config.VoucherConfig;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    public VoucherType load(String id, VoucherConfig.VoucherType fromConfig) {
        List<Action> actions = fromConfig.actions().stream()
                .map(msg ->
                        actionParser.parse(msg).orElseGet(() -> {
                            logger.warning(() -> "Could not parse action " + msg + " for voucher type " + id);
                            return null;
                        }))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String defaultData = fromConfig.defaultValue();
        String permission = fromConfig.permission();
        LangMessage receiveMessage = fromConfig.receiveMessage();
        LangMessage redeemMessage = fromConfig.redeemMessage();
        ItemConfig item = fromConfig.item();
        VoucherTypeSettings settings = new VoucherTypeSettings(actions, defaultData, item, permission, receiveMessage, redeemMessage);

        if (fromConfig.item() == null) {
            return new VoucherCodeType(id, settings);
        } else {
            return new NormalVoucherType(id, settings);
        }
    }

}
