package me.bristermitten.vouchers.data.voucher.persistence;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.bristermitten.mittenlib.files.json.ExtraTypeAdapter;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.data.voucher.VoucherRegistry;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class VouchersByIDTypeAdapter extends ExtraTypeAdapter<Set<Voucher>> {
    private final VoucherRegistry voucherRegistry;

    @Inject
    public VouchersByIDTypeAdapter(VoucherRegistry voucherRegistry) {
        this.voucherRegistry = voucherRegistry;
    }

    @Override
    public void write(JsonWriter out, Set<Voucher> value) throws IOException {
        out.beginArray();
        for (Voucher voucher : value) {
            out.value(voucher.getId().toString());
        }
        out.endArray();
    }

    @Override
    public Set<Voucher> read(JsonReader in) throws IOException {
        in.beginArray();
        Set<Voucher> vouchers = new HashSet<>();
        while (in.peek() != JsonToken.END_ARRAY) {
            String name = in.nextString();
            vouchers.add(voucherRegistry.lookup(UUID.fromString(name))
                    .orElseThrow(() -> new IllegalArgumentException("Unknown voucher " + name + "!")));
        }
        in.endArray();
        return vouchers;
    }

    @Override
    public Type type() {
        return Voucher.class;
    }
}
