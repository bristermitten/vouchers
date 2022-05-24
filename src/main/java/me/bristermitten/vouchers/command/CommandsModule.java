package me.bristermitten.vouchers.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class CommandsModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<BaseCommand> commandMultibinder = Multibinder.newSetBinder(binder(), BaseCommand.class);
        commandMultibinder.addBinding().to(ClaimBoxesCommand.class);
        commandMultibinder.addBinding().to(VouchersCommand.class);
        bind(BukkitCommandManager.class).toProvider(CommandManagerProvider.class)
                .asEagerSingleton();
    }
}
