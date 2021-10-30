package me.bristermitten.claimboxes.data;

import com.google.inject.Inject;
import me.bristermitten.claimboxes.data.persistence.SQLClaimBoxPersistence;
import me.bristermitten.mittenlib.persistence.Persistences;
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
