package me.bristermitten.vouchers.data.claimbox.persistence;

import me.bristermitten.mittenlib.util.Result;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.mittenlib.util.lambda.Functions;
import me.bristermitten.vouchers.data.claimbox.ClaimBox;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.data.voucher.VoucherRegistry;
import me.bristermitten.vouchers.database.Database;
import me.bristermitten.vouchers.persist.TableNameFactory;
import me.bristermitten.vouchers.util.Pair;
import me.bristermitten.vouchers.util.ResultSetUtil;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.*;

public class SQLClaimBoxPersistence implements ClaimBoxPersistence {
    private final Database database;

    private final TableNameFactory tnf;

    private final VoucherRegistry voucherRegistry;

    @Inject
    public SQLClaimBoxPersistence(Database database, TableNameFactory tnf, VoucherRegistry voucherRegistry) {
        this.database = database;
        this.tnf = tnf;
        this.voucherRegistry = voucherRegistry;
    }


    private String tableName() {
        return tnf.getTableName("ClaimBoxes");
    }

    @Override
    @NotNull
    public CompletableFuture<Unit> init() {
        return database.execute(
                "create table if not exists " + tableName() + "\n" +
                        "(\n" +
                        "    owner VARCHAR(36),\n" +
                        "    voucher_id VARCHAR(36) REFERENCES " + tnf.getTableName("Vouchers") + "(id) \n" +
                        ")\n");
    }

    @Override
    public @NotNull CompletableFuture<Unit> save(@NotNull ClaimBox claimBox) {
        return delete(claimBox.getOwner()).thenCompose(e ->
                database.runTransactionally(db -> db.runWithStatement("INSERT INTO " + tableName() + " (owner, voucher_id) values (?, ?)", preparedStatement -> {
                    for (Voucher voucher : claimBox.getVouchers()) {
                        preparedStatement.setString(1, claimBox.getOwner().toString());
                        preparedStatement.setString(2, voucher.getId().toString());
                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();
                    return null;
                })));
    }

    @Override
    public @NotNull CompletableFuture<Optional<ClaimBox>> load(@NotNull UUID id) {
        return database.query("SELECT * FROM " + tableName() + " WHERE owner = ?",
                statement -> statement.setString(1, id.toString()),
                results -> claimBoxesFromResultSet(results).stream().findFirst());
    }

    @NotNull
    private Collection<ClaimBox> claimBoxesFromResultSet(ResultSet results) {
        Map<UUID, Set<Voucher>> claimBoxes = ResultSetUtil.getRows(results)
                .map(row -> Result.runCatching(() -> {
                    UUID uuid = UUID.fromString(results.getString(1));
                    UUID voucherId = UUID.fromString(results.getString(2));
                    Voucher voucher = voucherRegistry.load(voucherId)
                            .join().orElseThrow(() -> new IllegalStateException("Voucher with ID " + voucherId + " not found"));
                    return Pair.of(uuid, voucher);
                }).getOrThrow())
                .collect(groupingBy(Pair::getFirst, mapping(Pair::getSecond, toSet())));

        return claimBoxes.entrySet().stream()
                .map(e -> new ClaimBox(e.getKey(), e.getValue()))
                .collect(toList());
    }

    @Override
    public @NotNull CompletableFuture<Unit> delete(@NotNull UUID id) {
        return database.execute("DELETE FROM " + tableName() + " WHERE owner = ?",
                statement -> statement.setString(1, id.toString()));
    }

    @NotNull
    public CompletableFuture<Unit> removeOne(@NotNull UUID id, Voucher voucher) {
        return database.execute("DELETE FROM " + tableName() + " WHERE owner = ? AND voucher_id = ? LIMIT 1",
                statement -> {
                    statement.setString(1, id.toString());
                    statement.setString(2, voucher.getId().toString());
                });
    }

    @NotNull
    public CompletableFuture<Unit> addOne(@NotNull UUID id, Voucher voucherId) {
        return database.execute("INSERT INTO " + tableName() + " (owner, voucher_id) values (?, ?)",
                preparedStatement -> {
                    preparedStatement.setString(1, id.toString());
                    preparedStatement.setString(2, voucherId.toString());
                });
    }

    @Override
    public @NotNull CompletableFuture<Collection<ClaimBox>> loadAll() {
        return database.query("SELECT * from " + tableName(),
                statement -> {
                },
                this::claimBoxesFromResultSet);
    }

    @Override
    @NotNull
    public CompletableFuture<Unit> saveAll(@NotNull Collection<ClaimBox> values) {
        return CompletableFuture
                .allOf(values.stream().map(this::save).toArray(CompletableFuture[]::new))
                .thenCompose(Functions.constant(Unit.unitFuture())); // TODO optimize this to be in 1 transaction
    }
}
