package me.bristermitten.vouchers.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.bristermitten.mittenlib.collections.Maps;
import me.bristermitten.mittenlib.commands.Command;
import me.bristermitten.mittenlib.util.lambda.Functions;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.data.voucher.VoucherRegistry;
import me.bristermitten.vouchers.data.voucher.VoucherUsageHandler;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeRegistry;
import me.bristermitten.vouchers.lang.VouchersLangService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

@CommandAlias("vouchers|voucher")
public class VouchersCommand extends Command {

    private final VoucherRegistry voucherRegistry;
    private final VoucherTypeRegistry voucherTypeRegistry;
    private final VouchersLangService langService;

    @Inject
    public VouchersCommand(VoucherRegistry voucherRegistry, VoucherTypeRegistry voucherTypeRegistry, VouchersLangService langService) {
        this.voucherRegistry = voucherRegistry;
        this.voucherTypeRegistry = voucherTypeRegistry;
        this.langService = langService;
    }

    @Subcommand("give")
    @CommandCompletion("@players @voucherIds")
    public void give(CommandSender sender, OnlinePlayer target, String voucherId, @Optional @Nullable String data) {
        Player targetPlayer = target.getPlayer();
        voucherTypeRegistry.get(voucherId).ifPresent(voucherType -> {
            Voucher voucher = voucherRegistry.create(voucherType, data);
            ItemStack voucherItem = voucherRegistry.createVoucherItem(voucher, targetPlayer);
            targetPlayer.getInventory().addItem(voucherItem);
            voucher.getType().getSettings().getReceiveMessage().ifPresent(receiveMessage ->
                    langService.send(targetPlayer, Functions.constant(receiveMessage),
                            Maps.of(VoucherUsageHandler.DATA_PLACEHOLDER, String.valueOf(data))));
        });
    }
}
