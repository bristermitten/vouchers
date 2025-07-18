package me.bristermitten.vouchers.command;

import me.bristermitten.mittenlib.collections.Maps;
import me.bristermitten.mittenlib.util.lambda.Functions;
import me.bristermitten.vouchers.data.claimbox.ClaimBoxManager;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.data.voucher.VoucherRegistry;
import me.bristermitten.vouchers.data.voucher.VoucherUsageHandler;
import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeRegistry;
import me.bristermitten.vouchers.lang.ClaimBoxesLangConfig;
import me.bristermitten.vouchers.lang.VouchersLangService;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.bristermitten.vouchers.command.CommandPlaceholders.PLAYER;

@Singleton
public class CommonVoucherMethods {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CommonVoucherMethods.class);
    private final VoucherTypeRegistry voucherTypeRegistry;

    private final VouchersLangService langService;
    private final VoucherRegistry voucherRegistry;
    private final ClaimBoxManager claimBoxManager;
    private final Logger logger;

    @Inject
    public CommonVoucherMethods(VoucherTypeRegistry voucherTypeRegistry, VouchersLangService langService, VoucherRegistry voucherRegistry, ClaimBoxManager claimBoxManager, Logger logger) {
        this.voucherTypeRegistry = voucherTypeRegistry;
        this.langService = langService;
        this.voucherRegistry = voucherRegistry;
        this.claimBoxManager = claimBoxManager;
        this.logger = logger;
    }

    public void giveVoucher(CommandSender sender, OfflinePlayer target, String voucherId, @Nullable String data, boolean inClaimbox) {
        Optional<VoucherType> typeOptional = voucherTypeRegistry.get(voucherId);
        if (!typeOptional.isPresent()) {
            langService.send(sender, conf -> conf.errors().unknownVoucher(), Maps.of("{id}", voucherId));
            return;
        }
        VoucherType type = typeOptional.get();
        Voucher voucher = voucherRegistry.create(type, data);
        if (!inClaimbox) {
            Player player = target.getPlayer();
            if (player == null) {
                langService.send(sender, conf -> conf.errors().playerOffline(),
                        Maps.of(VoucherUsageHandler.PLAYER_PLACEHOLDER, target.getName()));
                return;
            }
            ItemStack voucherItem = voucherRegistry.createVoucherItem(voucher, player);
            player.getInventory().addItem(voucherItem);
            voucher.getType().getSettings().getReceiveMessage().ifPresent(receiveMessage ->
                    langService.send(player, Functions.constant(receiveMessage),
                            Maps.of(VoucherUsageHandler.DATA_PLACEHOLDER, String.valueOf(voucher.getData()))));
            return;
        }
        claimBoxManager.getBox(target.getUniqueId())
                .thenCompose(box -> claimBoxManager.give(box, voucher))
                .thenAccept(box -> langService.send(sender, ClaimBoxesLangConfig::voucherGiven,
                        Maps.of(PLAYER, target.getName(), "{id}", voucherId)))
                .exceptionally(e -> {
                    logger.log(Level.SEVERE, e, () -> "Error giving voucher " + voucherId + " to " + target.getName());
                    return null;
                });
    }
}
