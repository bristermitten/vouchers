package me.bristermitten.vouchers.data.voucher.type;

import me.bristermitten.mittenlib.lang.LangMessage;
import me.bristermitten.vouchers.actions.Action;
import me.bristermitten.vouchers.config.ItemConfig;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

/**
 * Stores all settings for voucher types
 */
public class VoucherTypeSettings {
    private final Collection<Action> actions;
    private final @Nullable String defaultData;
    /**
     * Descriptor for the voucher item. If null, then the voucher is a voucher code only
     */
    private final @Nullable ItemConfig itemDescriptor;
    private final @Nullable String permission;

    private final @Nullable LangMessage receiveMessage;
    private final @Nullable LangMessage redeemMessage;

    public VoucherTypeSettings(Collection<Action> actions, @Nullable String defaultData, @Nullable ItemConfig itemDescriptor, @Nullable String permission, @Nullable LangMessage receiveMessage, @Nullable LangMessage redeemMessage) {
        this.actions = new HashSet<>(actions);
        this.defaultData = defaultData;
        this.itemDescriptor = itemDescriptor;
        this.permission = permission;
        this.receiveMessage = receiveMessage;
        this.redeemMessage = redeemMessage;
    }

    public @Unmodifiable Collection<Action> getActions() {
        return Collections.unmodifiableCollection(actions);
    }

    public Optional<String> getDefaultData() {
        return Optional.ofNullable(defaultData);
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public Optional<ItemConfig> getItemDescriptor() {
        return Optional.ofNullable(itemDescriptor);
    }

    public Optional<LangMessage> getReceiveMessage() {
        return Optional.ofNullable(receiveMessage);
    }

    public Optional<LangMessage> getRedeemMessage() {
        return Optional.ofNullable(redeemMessage);
    }

}
