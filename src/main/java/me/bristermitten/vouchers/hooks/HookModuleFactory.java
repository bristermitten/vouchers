package me.bristermitten.vouchers.hooks;

import com.google.inject.Module;
import me.bristermitten.vouchers.hooks.vault.VaultModule;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HookModuleFactory {

    public Collection<Module> discoverHookModules() {
        List<Module> modules = new ArrayList<>();

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            modules.add(new VaultModule());
        }

        return modules;
    }
}
