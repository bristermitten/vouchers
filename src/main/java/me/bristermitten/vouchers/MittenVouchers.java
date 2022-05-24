package me.bristermitten.vouchers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bristermitten.mittenlib.MittenLib;
import me.bristermitten.mittenlib.minimessage.MiniMessageModule;
import me.bristermitten.mittenlib.papi.PAPIModule;
import me.bristermitten.vouchers.actions.ActionModule;
import me.bristermitten.vouchers.command.CommandsModule;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.claimbox.ClaimBoxDataModule;
import me.bristermitten.vouchers.data.claimbox.ClaimBoxPersistence;
import me.bristermitten.vouchers.data.voucher.VoucherConfig;
import me.bristermitten.vouchers.data.voucher.VoucherUsageListener;
import me.bristermitten.vouchers.database.DatabaseModule;
import me.bristermitten.vouchers.lang.ClaimBoxesLangConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;

public class MittenVouchers extends JavaPlugin {
    @Inject
    private ClaimboxUpdateTask autoSaveTask;

    @Override
    public void onEnable() {

        final Injector injector = MittenLib.withDefaults(this)
                .addConfigModules(ClaimBoxesConfig.CONFIG, ClaimBoxesLangConfig.CONFIG, VoucherConfig.CONFIG)
                .addModules(
                        new DatabaseModule(),
                        new ClaimBoxDataModule(),
                        new ActionModule(),
                        new MiniMessageModule(),
                        new PAPIModule(),
                        new CommandsModule())
                .build();

        injector.injectMembers(this);

        autoSaveTask.schedule();

        injector.getInstance(ClaimBoxPersistence.class).init()
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });

        VoucherUsageListener usageListener = injector.getInstance(VoucherUsageListener.class);
        Bukkit.getPluginManager().registerEvents(usageListener, this);
    }

    @Override
    public void onDisable() {
        try {
            autoSaveTask.run().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
