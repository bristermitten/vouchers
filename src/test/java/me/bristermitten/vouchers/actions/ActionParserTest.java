package me.bristermitten.vouchers.actions;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActionParserTest {

    @Test
    void parse() {
        ActionTypeRegistry actionTypeRegistry = new ActionTypeRegistry(
                Collections.singleton(new NOOPAction())
        );
        ActionParser parser = new ActionParser(actionTypeRegistry);

        String input = "[NOOP] data";
        Optional<Action> parse = parser.parse(input);
        assertTrue(parse.isPresent());
        Action action = parse.get();

        assertEquals("NOOP", action.getType().getTag());
        assertEquals("data", action.getData());
    }
}
