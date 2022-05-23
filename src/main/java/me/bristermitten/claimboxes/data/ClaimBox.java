package me.bristermitten.claimboxes.data;

import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimBox claimBox = (ClaimBox) o;
        return Objects.equals(getOwner(), claimBox.getOwner()) && Objects.equals(getVoucherIds(), claimBox.getVoucherIds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getVoucherIds());
    }

    @Override
    public String toString() {
        return "ClaimBox{" + "owner=" + owner + ", voucherIds=" + voucherIds + '}';
    }
}
