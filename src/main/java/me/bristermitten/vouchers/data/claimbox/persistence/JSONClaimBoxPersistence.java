package me.bristermitten.vouchers.data.claimbox.persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.data.claimbox.ClaimBox;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.persist.JSONPersistence;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * JSON-based implementation of ClaimBoxPersistence.
 * Stores each ClaimBox in its own JSON file.
 */
public class JSONClaimBoxPersistence extends JSONPersistence<UUID, ClaimBox> implements ClaimBoxPersistence {
    private static final String FILE_PATH = "ClaimBoxes";


    @Inject
    public JSONClaimBoxPersistence(Plugin plugin, Gson gson) {
        super(plugin.getDataFolder().toPath().resolve(FILE_PATH), gson, TypeToken.get(ClaimBox.class));
    }

    @Override
    protected Path getFilePath(ClaimBox value) {
        return getFilePathFromId(value.getOwner());
    }

    @Override
    protected Path getFilePathFromId(UUID uuid) {
        return filePath.resolve(uuid + ".json");
    }


    @Override
    public @NotNull CompletableFuture<Unit> removeOne(@NotNull UUID id, Voucher voucher) {
        return load(id)
                .thenCompose(optionalBox -> {
                    if (!optionalBox.isPresent()) {
                        return Unit.unitFuture(); // Nothing to remove from
                    }

                    ClaimBox box = optionalBox.get();
                    box.editVouchers(vouchers -> vouchers.remove(voucher));
                    return save(box);
                });
    }

    @Override
    public @NotNull CompletableFuture<Unit> addOne(@NotNull UUID id, Voucher voucher) {
        return load(id)
                .thenCompose(optionalBox -> {
                    ClaimBox box;
                    box = optionalBox
                            .orElseGet(() -> new ClaimBox(id, new HashSet<>()));

                    box.editVouchers(vouchers -> vouchers.add(voucher));
                    return save(box);
                });
    }


}
