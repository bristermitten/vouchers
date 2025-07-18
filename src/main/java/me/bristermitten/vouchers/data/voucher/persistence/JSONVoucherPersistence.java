package me.bristermitten.vouchers.data.voucher.persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.database.RuntimePersistException;
import me.bristermitten.vouchers.persist.JSONPersistence;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JSONVoucherPersistence extends JSONPersistence<UUID, Voucher> implements VoucherPersistence {
    private static final String FILE_PATH = "Vouchers";


    @Inject
    public JSONVoucherPersistence(Plugin plugin, Gson gson) {
        super(plugin.getDataFolder().toPath().resolve(FILE_PATH), gson, TypeToken.get(Voucher.class));
    }

    @Override
    public @NotNull CompletableFuture<Unit> init() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.createDirectories(filePath);
            } catch (IOException e) {
                throw new RuntimePersistException(e);
            }
            return Unit.UNIT;
        });

    }

    @Override
    protected Path getFilePath(Voucher value) {
        return getFilePathFromId(value.getId());
    }

    @Override
    protected Path getFilePathFromId(UUID uuid) {
        return filePath.resolve(uuid + ".json");
    }

}
