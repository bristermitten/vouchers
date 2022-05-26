package me.bristermitten.vouchers.data.voucher.persistence;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.bristermitten.mittenlib.files.json.ExtraTypeAdapter;
import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeRegistry;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Type;

public class VoucherTypeTypeAdapter extends ExtraTypeAdapter<VoucherType> {
    private final VoucherTypeRegistry registry;

    @Inject
    public VoucherTypeTypeAdapter(VoucherTypeRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void write(JsonWriter out, VoucherType value) throws IOException {
        out.value(value.getId());
    }

    @Override
    public VoucherType read(JsonReader in) throws IOException {
        String id = in.nextString();
        return registry.get(id)
                .orElseThrow(() -> new IllegalArgumentException("Unknown voucher type: " + id));
    }

    @Override
    public Type type() {
        return VoucherType.class;
    }
}
