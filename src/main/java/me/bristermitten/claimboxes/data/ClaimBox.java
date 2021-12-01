package me.bristermitten.claimboxes.data;

import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ClaimBox {
    private final UUID owner;
    private final List<String> voucherIds;

    public ClaimBox(UUID owner, List<String> voucherIds) {
        this.owner = owner;
        this.voucherIds = new ArrayList<>(voucherIds);
    }


    public UUID getOwner() {
        return owner;
    }

    @UnmodifiableView
    public List<String> getVoucherIds() {
        return Collections.unmodifiableList(voucherIds);
    }

    public void editVoucherIds(Consumer<List<String>> consumer) {
        synchronized (this) {
            consumer.accept(voucherIds);
        }
    }

    @Override
    public String toString() {
        return "ClaimBox{" + "owner=" + owner + ", voucherIds=" + voucherIds + '}';
    }
}
