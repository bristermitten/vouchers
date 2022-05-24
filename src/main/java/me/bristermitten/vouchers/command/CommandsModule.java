package me.bristermitten.vouchers.command;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.AbstractModule;

public class CommandsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BukkitCommandManager.class).toProvider(CommandManagerProvider.class);
    }
}
