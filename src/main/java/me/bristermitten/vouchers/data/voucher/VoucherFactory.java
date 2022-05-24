package me.bristermitten.vouchers.data.voucher;

import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import me.bristermitten.mittenlib.lang.format.hook.SimpleFormattingHook;
import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VoucherFactory {

    private static final String DATA_PLACEHOLDER = "{value}";

    private final MessageFormatter messageFormatter;

    @Inject
    public VoucherFactory(MessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
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
        return new Voucher(UUID.randomUUID(), data, type);
    }

    public ItemStack createVoucherItem(Voucher voucher, @Nullable final String data, @Nullable Player player) {
        ItemDescriptor descriptor = voucher.getType().getItemDescriptor();
        if (descriptor == null) {
            throw new IllegalArgumentException("Voucher type " + voucher.getType().getId() + " can't create items");
        }
        final Material type = descriptor.getType();
        final String name = descriptor.getName();
        final List<String> lore = descriptor.getLore();

        MessageFormatter withValuePlaceholder = data == null ? messageFormatter : messageFormatter.withExtraHooks(
                new SimpleFormattingHook((s, p) -> s.replace(DATA_PLACEHOLDER, data))
        );
        final ItemStack item = new ItemStack(type);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return item;
        }
        if (name != null) {
            itemMeta.setDisplayName(withValuePlaceholder.preFormat(name, player));
        }
        if (lore != null) {
            itemMeta.setLore(lore.stream()
                    .map(s -> withValuePlaceholder.preFormat(s, player))
                    .collect(Collectors.toList()));
        }
        item.setItemMeta(itemMeta);
        return item;
    }
}

