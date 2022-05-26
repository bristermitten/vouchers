package me.bristermitten.vouchers.actions;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.inject.Inject;
import java.io.IOException;

public class ActionTypeAdapter extends TypeAdapter<Action> {
    private final ActionParser parser;

    @Inject
    public ActionTypeAdapter(ActionParser parser) {
        this.parser = parser;
    }

    @Override
    public void write(JsonWriter out, Action value) throws IOException {
        out.value(value.serialize());
    }

    @Override
    public Action read(JsonReader in) throws IOException {
        String input = in.nextString();
        return parser.parse(input)
                .orElseThrow(() -> new IllegalArgumentException("Could not parse action " + input));
    }
}
