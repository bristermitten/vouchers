package me.bristermitten.vouchers.config.data.persistence;

import com.google.common.base.Functions;
import com.google.inject.Inject;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.config.data.ClaimBox;
import me.bristermitten.vouchers.config.data.ClaimBoxPersistence;
import me.bristermitten.vouchers.database.Database;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SQLClaimBoxPersistence implements ClaimBoxPersistence {
    private final Database database;

    @Inject
    public SQLClaimBoxPersistence(Database database) {
        this.database = database;
    }


    @Override
    @NotNull
    public CompletableFuture<Unit> init() {
        return database.execute(
                "create table if not exists claimbox_voucher_ids\n" +
                        "(\n" +
                        "    owner VARCHAR(36),\n" +
                        "    voucher_id  text\n" +
                        ")\n");
    }

    @Override
    public @NotNull CompletableFuture<Unit> cleanup() {
        return Unit.unitFuture(); // Nothing to do
    }

    @Override
    public @NotNull CompletableFuture<Unit> save(@NotNull ClaimBox claimBox) {
        return delete(claimBox.getOwner()).thenCompose(e ->
                database.runTransactionally(db -> db.runWithStatement("INSERT INTO claimbox_voucher_ids (owner, voucher_id) values (?, ?)", preparedStatement -> {
                    for (String voucherId : claimBox.getVoucherIds()) {
                        preparedStatement.setString(1, claimBox.getOwner().toString());
                        preparedStatement.setString(2, voucherId);
                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();
                    return null;
                })));
    }

    @Override
    public @NotNull CompletableFuture<Optional<ClaimBox>> load(@NotNull UUID id) {
        return database.query("SELECT * FROM claimbox_voucher_ids WHERE owner = ?",
                statement -> statement.setString(1, id.toString()),
                results -> claimBoxesFromResultSet(results).stream().findFirst());
    }

    @NotNull
    private Collection<ClaimBox> claimBoxesFromResultSet(ResultSet results) throws SQLException {
        final Map<UUID, List<String>> byUUID = new HashMap<>();

        while (results.next()) {
            UUID uuid = UUID.fromString(results.getString(1));
            final List<String> claimBox = byUUID.computeIfAbsent(uuid, a -> new ArrayList<>());
            claimBox.add(results.getString(2));
        }

        return byUUID.entrySet().stream()
                .map(e -> new ClaimBox(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull CompletableFuture<Unit> delete(@NotNull UUID id) {
        return database.execute("DELETE FROM claimbox_voucher_ids WHERE owner = ?",
                statement -> statement.setString(1, id.toString()));
    }

    @NotNull
    public CompletableFuture<Unit> removeOne(@NotNull UUID id, String voucherId) {
        return database.execute("DELETE FROM claimbox_voucher_ids WHERE owner = ? AND voucher_id = ? LIMIT 1",
                statement -> {
                    statement.setString(1, id.toString());
                    statement.setString(2, voucherId);
                });
    }

    @NotNull
    public CompletableFuture<Unit> addOne(@NotNull UUID id, String voucherId) {
        return database.execute("INSERT INTO claimbox_voucher_ids (owner, voucher_id) values (?, ?)",
                preparedStatement -> {
                    preparedStatement.setString(1, id.toString());
                    preparedStatement.setString(2, voucherId);
                });
    }

    @Override
    public @NotNull CompletableFuture<Collection<ClaimBox>> loadAll() {
        return database.query("SELECT * from claimbox_voucher_ids",
                statement -> {
                },
                this::claimBoxesFromResultSet);
    }

    @Override
    @NotNull
    public CompletableFuture<Unit> saveAll(@NotNull Collection<ClaimBox> values) {
        return CompletableFuture
                .allOf(values.stream().map(this::save).toArray(CompletableFuture[]::new))
                .thenCompose(Functions.constant(Unit.unitFuture()));
    }
}
