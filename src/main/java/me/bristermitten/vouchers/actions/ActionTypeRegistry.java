package me.bristermitten.vouchers.actions;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@Singleton
public class ActionTypeRegistry {
    private final Map<String, ActionType<?>> actionTypes = new HashMap<>();
    private final Logger logger;

    @Inject
    public ActionTypeRegistry(Set<ActionType<?>> standardActionTypes, Logger logger) {
        this.logger = logger;
        for (ActionType<?> actionType : standardActionTypes) {
            register(actionType);
        }
    }

    public Optional<ActionType<?>> getByTag(@NotNull String tag) {
        return Optional.ofNullable(actionTypes.get(tag));
    }

    public void register(ActionType<?> actionType) {
        if (actionTypes.put(actionType.getTag(), actionType) != null) {
            logger.warning(() ->
                    "Multiple action types registered with tag " + actionType.getTag() + ".");
        }
    }

    public void unregister(ActionType<?> actionType) {
        actionTypes.remove(actionType.getTag());
    }

}
