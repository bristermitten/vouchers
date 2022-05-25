package me.bristermitten.vouchers.data.claimbox;

import me.bristermitten.vouchers.data.voucher.Voucher;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Consumer;

public class ClaimBox {
    private final UUID owner;
    private final Set<Voucher> vouchers;

    public ClaimBox(UUID owner, Set<Voucher> vouchers) {
        this.owner = owner;
        this.vouchers = new HashSet<>(vouchers);
    }


    public UUID getOwner() {
        return owner;
    }

    @UnmodifiableView
    public Set<Voucher> getVouchers() {
        return Collections.unmodifiableSet(vouchers);
    }

    public void editVouchers(Consumer<Set<Voucher>> consumer) {
        synchronized (this) {
            consumer.accept(vouchers);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimBox claimBox = (ClaimBox) o;
        return Objects.equals(getOwner(), claimBox.getOwner()) && Objects.equals(getVouchers(), claimBox.getVouchers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getVouchers());
    }

    @Override
    public String toString() {
        return "ClaimBox{" + "owner=" + owner + ", vouchers=" + vouchers + '}';
    }
}
