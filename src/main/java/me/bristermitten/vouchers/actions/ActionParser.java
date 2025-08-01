package me.bristermitten.vouchers.actions;

import me.bristermitten.mittenlib.util.Null;
import me.bristermitten.vouchers.actions.validate.ValidationResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionParser {
    private static final Pattern TAG_PATTERN =
            Pattern.compile("\\[([A-Z_]+)]( .+)?");

    private final ActionTypeRegistry actionTypeRegistry;

    @Inject
    public ActionParser(ActionTypeRegistry actionTypeRegistry) {
        this.actionTypeRegistry = actionTypeRegistry;
    }

    public Optional<Action> parse(@NotNull String input) {
        Matcher matcher = TAG_PATTERN.matcher(input);
        if (!matcher.find()) {
            return Optional.empty();
        }
        final String tag = matcher.group(1);
        final @Nullable String content = Null.map(matcher.group(2), String::trim);


        return actionTypeRegistry.getByTag(tag)
                .map(type -> new Action(type, content));
    }
}
