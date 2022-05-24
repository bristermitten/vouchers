package me.bristermitten.vouchers;

import com.google.inject.AbstractModule;
import net.luckperms.api.LuckPerms;

public class LuckPermsModule extends AbstractModule {
    private final LuckPerms luckPerms;

    public LuckPermsModule(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    protected void configure() {
        bind(LuckPerms.class).toInstance(luckPerms);
    }
}
