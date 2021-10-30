package me.bristermitten.claimboxes.data;

import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    List<String> getMutableVoucherIds() {
        return voucherIds;
    }
}
