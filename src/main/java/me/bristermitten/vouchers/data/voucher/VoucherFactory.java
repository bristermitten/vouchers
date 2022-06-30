package me.bristermitten.vouchers.data.voucher;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import me.bristermitten.mittenlib.lang.format.hook.SimpleFormattingHook;
import me.bristermitten.vouchers.config.ItemConfig;
import me.bristermitten.vouchers.config.ItemCreator;
import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;

public class VoucherFactory {

    private final MessageFormatter messageFormatter;

    private final ItemCreator itemCreator;

    @Inject
    public VoucherFactory(MessageFormatter messageFormatter, ItemCreator itemCreator) {
        this.messageFormatter = messageFormatter;
        this.itemCreator = itemCreator;
    }

    /**
     * Creates and <b>does not</b> save a voucher based on a type and data
     * If you want to create vouchers normally, use {@link VoucherRegistry#create(VoucherType, String)}
     * <p>
     * Aside from random UUID generation, this function is completely pure.
     *
     * @param type the type of voucher to create
     * @param data the data to give the voucher
     * @return the created voucher
     */
    public Voucher createVoucher(VoucherType type, @Nullable String data) {
        data = data == null ? type.getSettings().getDefaultData().orElse(null) : data;
        return new Voucher(UUID.randomUUID(), data, type);
    }

    public ItemStack createVoucherItem(Voucher voucher, @Nullable final String data, @Nullable OfflinePlayer player) {
        ItemConfig descriptor = voucher.getType().getSettings().getItemDescriptor().orElse(null);
        if (descriptor == null) {
            throw new IllegalArgumentException("Voucher type " + voucher.getType().getId() + " can't create items");
        }
        MessageFormatter withValuePlaceholder = data == null ? messageFormatter : messageFormatter.withExtraHooks(
                new SimpleFormattingHook((s, p) -> s.replace(VoucherUsageHandler.DATA_PLACEHOLDER, data))
        );
        if (player != null) {
            withValuePlaceholder = withValuePlaceholder.withExtraHooks(
                    new SimpleFormattingHook((s, p) -> s.replace(VoucherUsageHandler.PLAYER_PLACEHOLDER, player.getName()))
            );

        }
        ItemStack item = itemCreator.toItem(withValuePlaceholder, descriptor, player);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(Voucher.NBT_KEY, voucher.getId().toString());
        return nbtItem.getItem();
    }
}

