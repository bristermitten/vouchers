package me.bristermitten.claimboxes;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bristermitten.claimboxes.command.ClaimBoxesCommand;
import me.bristermitten.claimboxes.command.CommandsModule;
import me.bristermitten.claimboxes.config.ClaimBoxesConfig;
import me.bristermitten.claimboxes.data.ClaimBoxDataModule;
import me.bristermitten.claimboxes.data.ClaimBoxPersistence;
import me.bristermitten.claimboxes.database.DatabaseModule;
import me.bristermitten.claimboxes.lang.ClaimBoxesLangConfig;
import me.bristermitten.mittenlib.MittenLib;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;

public class ClaimBoxes extends JavaPlugin {
    @Inject
    private ClaimboxUpdateTask autoSaveTask;

    @Override
    public void onEnable() {

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            getLogger().severe("LuckPerms not installed, ClaimBoxes shutting down.");
            setEnabled(false);
            return;
        }

        final LuckPerms luckPerms = provider.getProvider();

        final Injector injector = MittenLib.withDefaults(this)
                .addConfigModules(ClaimBoxesConfig.CONFIG, ClaimBoxesLangConfig.CONFIG)
                .addModules(
                        new LuckPermsModule(luckPerms),
                        new DatabaseModule(),
                        new ClaimBoxDataModule(),
                        new CommandsModule())
                .build();

        injector.getInstance(BukkitCommandManager.class).registerCommand(injector.getInstance(ClaimBoxesCommand.class));

        injector.injectMembers(this);

        autoSaveTask.schedule();

        injector.getInstance(ClaimBoxPersistence.class).init()
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
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
