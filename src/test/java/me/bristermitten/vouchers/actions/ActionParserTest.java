package me.bristermitten.vouchers.actions;

import me.bristermitten.vouchers.actions.standard.BroadcastAction;
import me.bristermitten.vouchers.actions.validate.ValidationResponse;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ActionParserTest {

    @Test
    void parse() {
        ActionTypeRegistry actionTypeRegistry = new ActionTypeRegistry(
                Collections.singleton(new NOOPAction()),
                Logger.getGlobal()
        );
        ActionParser parser = new ActionParser(actionTypeRegistry);

        String input = "[NOOP] data";
        Optional<Action> parse = parser.parse(input);
        assertTrue(parse.isPresent());
        Action action = parse.get();

        assertEquals("NOOP", action.getType().getTag());
        assertEquals("data", action.getArgument());
    }

    @Test
    void parseInvalid() {
        ActionTypeRegistry actionTypeRegistry = new ActionTypeRegistry(
                Collections.singleton(new BroadcastAction(null, null)),
                Logger.getGlobal()
        );
        ActionParser parser = new ActionParser(actionTypeRegistry);

        String input = "[BROADCAST]";
        Optional<Action> parsed = parser.parse(input);
        assertTrue(parsed.isPresent());
        Action parse = parsed.get();
        ValidationResponse<?> validationResponse = parse.validateWith(null, null);
        assertFalse(validationResponse.isOk());
        assertFalse(validationResponse.getErrors().isEmpty());
        assertEquals(1, validationResponse.getErrors().size());
    }
}
