package me.bristermitten.vouchers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import me.bristermitten.mittenlib.MittenLib;
import me.bristermitten.mittenlib.lang.LangModule;
import me.bristermitten.mittenlib.minimessage.MiniMessageModule;
import me.bristermitten.mittenlib.papi.PAPIModule;
import me.bristermitten.mittenlib.util.Futures;
import me.bristermitten.mittenlib.watcher.FileWatcherModule;
import me.bristermitten.vouchers.actions.ActionModule;
import me.bristermitten.vouchers.command.VouchersCommandsModule;
import me.bristermitten.vouchers.config.ClaimBoxesConfigImpl;
import me.bristermitten.vouchers.config.VoucherConfig;
import me.bristermitten.vouchers.data.claimbox.ClaimBoxDataModule;
import me.bristermitten.vouchers.data.claimbox.persistence.ClaimBoxPersistence;
import me.bristermitten.vouchers.data.voucher.VoucherModule;
import me.bristermitten.vouchers.data.voucher.VoucherUsageListener;
import me.bristermitten.vouchers.data.voucher.persistence.VoucherPersistence;
import me.bristermitten.vouchers.database.DatabaseModule;
import me.bristermitten.vouchers.hooks.DefaultHookModule;
import me.bristermitten.vouchers.hooks.HookModuleFactory;
import me.bristermitten.vouchers.lang.ClaimBoxesLangConfig;
import me.bristermitten.vouchers.persist.Persistence;
import me.bristermitten.vouchers.util.GlowEnchant;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MittenVouchers extends JavaPlugin {
    @Inject
    private ClaimboxUpdateTask autoSaveTask;

    private List<Persistence<?, ?>> persistences = new ArrayList<>();

    public MittenVouchers(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public MittenVouchers() {
    }

    @Override
    public void onEnable() {
        HookModuleFactory hookModuleFactory = new HookModuleFactory();
        final Injector injector = MittenLib.withDefaults(this)
                .addConfigModules(ClaimBoxesConfigImpl.CONFIG, ClaimBoxesLangConfig.CONFIG, VoucherConfig.CONFIG)
                .addModules(
                        Modules.disableCircularProxiesModule(),
                        new DatabaseModule(),
                        new ClaimBoxDataModule(),
                        new VoucherModule(),
                        new ActionModule(),
                        new PAPIModule(),
                        new FileWatcherModule(),
                        new VouchersCommandsModule(),
                        Modules.override(new DefaultHookModule())
                                .with(hookModuleFactory.discoverHookModules()),
                        new LangModule(),
                        new MiniMessageModule())
                .build();

        injector.injectMembers(this);

        autoSaveTask.schedule();

        this.persistences = Arrays.asList(
                injector.getInstance(VoucherPersistence.class),
                injector.getInstance(ClaimBoxPersistence.class)
        );
        try {
            GlowEnchant.registerGlow();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        Instant startTime = Instant.now();
        Futures.sequence(persistences.stream().map(Persistence::init).collect(Collectors.toList()))
                .whenComplete((v, e) -> {
                    if (e != null) {
                        getLogger().log(Level.SEVERE, "Failed to initialize persistence!", e);
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
