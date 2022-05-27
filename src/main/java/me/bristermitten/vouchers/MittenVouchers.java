package me.bristermitten.vouchers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bristermitten.mittenlib.MittenLib;
import me.bristermitten.mittenlib.minimessage.MiniMessageModule;
import me.bristermitten.mittenlib.papi.PAPIModule;
import me.bristermitten.mittenlib.util.Futures;
import me.bristermitten.vouchers.actions.ActionModule;
import me.bristermitten.vouchers.command.CommandsModule;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.config.VoucherConfig;
import me.bristermitten.vouchers.data.claimbox.ClaimBoxDataModule;
import me.bristermitten.vouchers.data.claimbox.persistence.ClaimBoxPersistence;
import me.bristermitten.vouchers.data.voucher.VoucherModule;
import me.bristermitten.vouchers.data.voucher.VoucherUsageListener;
import me.bristermitten.vouchers.data.voucher.persistence.VoucherPersistence;
import me.bristermitten.vouchers.database.DatabaseModule;
import me.bristermitten.vouchers.lang.ClaimBoxesLangConfig;
import me.bristermitten.vouchers.persist.Persistence;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MittenVouchers extends JavaPlugin {
    @Inject
    private ClaimboxUpdateTask autoSaveTask;

    private List<Persistence<?, ?>> persistences;

    @Override
    public void onEnable() {

        final Injector injector = MittenLib.withDefaults(this)
                .addConfigModules(ClaimBoxesConfig.CONFIG, ClaimBoxesLangConfig.CONFIG, VoucherConfig.CONFIG)
                .addModules(
                        new DatabaseModule(),
                        new ClaimBoxDataModule(),
                        new VoucherModule(),
                        new ActionModule(),
                        new MiniMessageModule(),
                        new PAPIModule(),
                        new CommandsModule())
                .build();

        injector.injectMembers(this);

        autoSaveTask.schedule();

        this.persistences = Arrays.asList(
                injector.getInstance(VoucherPersistence.class),
                injector.getInstance(ClaimBoxPersistence.class)
        );
        Instant startTime = Instant.now();
        Futures.sequence(persistences.stream().map(Persistence::init).collect(Collectors.toList()))
                .whenComplete((v, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                        Bukkit.getPluginManager().disablePlugin(this);
                        return;
                    }
                    Instant endTime = Instant.now();
                    Duration d = Duration.between(startTime, endTime);
                    getLogger().info(() -> "Persistence initialization took " + d.toMillis() + "ms");
                });

        VoucherUsageListener usageListener = injector.getInstance(VoucherUsageListener.class);
        Bukkit.getPluginManager().registerEvents(usageListener, this);
    }

    @Override
    public void onDisable() {
        persistences.forEach(p -> p.cleanup().join());
        try {
            autoSaveTask.run().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
