package me.bristermitten.vouchers.command;

import co.aikar.commands.annotation.*;
import me.bristermitten.mittenlib.commands.Command;
import me.bristermitten.vouchers.hooks.PermissionChecker;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

@CommandAlias("vouchers|voucher")
public class VouchersCommand extends Command {

    private final CommonVoucherMethods commonVoucherMethods;
    private final PermissionChecker permissionChecker;

    @Inject
    public VouchersCommand(CommonVoucherMethods commonVoucherMethods, PermissionChecker permissionChecker) {
        this.commonVoucherMethods = commonVoucherMethods;
        this.permissionChecker = permissionChecker;
    }

    @Subcommand("give")
    @CommandCompletion("@players @voucherIds")
    public void give(CommandSender sender, OfflinePlayer target, String voucherId, @Optional @Nullable String data) {
        boolean useClaimBox;
        if (target.isOnline()) {
            useClaimBox = false;
        } else {
            if (!target.hasPlayedBefore() || target.getName() == null) {
                useClaimBox = true; // Add it just to be safe, it's not like they can open it anyway
            } else {
                useClaimBox = permissionChecker.has(target, "mittenvouchers.claimbox");
            }
        }
        commonVoucherMethods.giveVoucher(sender, target, voucherId, data, useClaimBox);
    }

    @Subcommand("help")
    public void help() {
        //noinspection deprecation
        showCommandHelp();
    }
}
