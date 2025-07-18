package me.bristermitten.vouchers.actions.standard;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import me.bristermitten.vouchers.actions.ActionType;

public class StandardCommandActions extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<ActionType<?>> actionTypeMultibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<ActionType<?>>(){});
        actionTypeMultibinder.addBinding().to(OPCommandAction.class);
        actionTypeMultibinder.addBinding().to(BroadcastAction.class);
    }
}
