package me.bristermitten.vouchers.hooks.vault;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import me.bristermitten.vouchers.actions.ActionType;
import me.bristermitten.vouchers.hooks.PermissionChecker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PermissionChecker.class).to(VaultPermissionChecker.class);


        Multibinder<ActionType<?>> multibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<ActionType<?>>() {
        });
        multibinder
                .addBinding()
                .to(EconomyGiveAction.class);
    }

    @Provides
    public Economy provideEconomy() {
        RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (registration == null) {
            throw new IllegalStateException("Vault Economy service is not registered. Do you have Vault and an economy plugin installed?");
        }

        return registration.getProvider();
    }
}
