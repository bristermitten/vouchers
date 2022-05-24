package me.bristermitten.vouchers.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.data.voucher.VoucherRegistry;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

@CommandAlias("vouchers|voucher")
public class VouchersCommand extends BaseCommand {

    private final VoucherRegistry voucherRegistry;
    private final VoucherTypeRegistry voucherTypeRegistry;

    @Inject
    public VouchersCommand(VoucherRegistry voucherRegistry, VoucherTypeRegistry voucherTypeRegistry) {
        this.voucherRegistry = voucherRegistry;
        this.voucherTypeRegistry = voucherTypeRegistry;
    }

    @Subcommand("give")
    public void give(CommandSender sender, OnlinePlayer target, String voucherId, @Optional @Nullable String data) {
        Player targetPlayer = target.getPlayer();
        voucherTypeRegistry.get(voucherId).ifPresent(voucherType -> {
            Voucher voucher = voucherRegistry.create(voucherType, data);
            ItemStack voucherItem = voucherRegistry.createVoucherItem(voucher, targetPlayer);
            targetPlayer.getInventory().addItem(voucherItem);
        });
    }
}
