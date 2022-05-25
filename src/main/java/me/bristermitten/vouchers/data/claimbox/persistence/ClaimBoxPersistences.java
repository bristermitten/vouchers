package me.bristermitten.vouchers.data.claimbox.persistence;

import com.google.inject.Inject;
import me.bristermitten.vouchers.data.claimbox.ClaimBox;
import me.bristermitten.vouchers.persist.Persistences;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class ClaimBoxPersistences implements Persistences<UUID, ClaimBox, ClaimBoxPersistence> {
    private final SQLClaimBoxPersistence sql;

    @Inject
    public ClaimBoxPersistences(SQLClaimBoxPersistence sql) {
        this.sql = sql;
    }

    @Override
    @NotNull
    public Optional<ClaimBoxPersistence> mariadb() {
        return Optional.of(sql);
    }
}
