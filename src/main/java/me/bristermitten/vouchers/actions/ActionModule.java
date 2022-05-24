package me.bristermitten.vouchers.actions;

import com.google.inject.AbstractModule;
import me.bristermitten.vouchers.actions.standard.StandardCommandActions;

public class ActionModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new StandardCommandActions());
    }
}
