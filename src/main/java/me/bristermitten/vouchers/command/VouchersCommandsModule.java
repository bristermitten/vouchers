package me.bristermitten.vouchers.command;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import me.bristermitten.mittenlib.commands.Command;
import me.bristermitten.mittenlib.commands.CommandsModule;
import me.bristermitten.mittenlib.commands.handlers.ArgumentContext;
import me.bristermitten.mittenlib.commands.handlers.TabCompleter;

public class VouchersCommandsModule extends CommandsModule {

    @Override
    protected void configure() {
        super.configure();
        Multibinder<Command> commandMultibinder = Multibinder.newSetBinder(binder(), Command.class);
        commandMultibinder.addBinding().to(ClaimBoxesCommand.class);
        commandMultibinder.addBinding().to(VouchersCommand.class);


        Multibinder<TabCompleter> tabCompleterMultibinder = Multibinder.newSetBinder(binder(), TabCompleter.class);
        tabCompleterMultibinder.addBinding().to(OfflinePlayerHandler.class);
        tabCompleterMultibinder.addBinding().to(VoucherIDCompletion.class);

        Multibinder<ArgumentContext<?>> argumentContextMultibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<ArgumentContext<?>>() {
        });
        argumentContextMultibinder.addBinding().to(OfflinePlayerHandler.class);
    }
}
