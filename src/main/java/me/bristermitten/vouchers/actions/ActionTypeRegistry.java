package me.bristermitten.vouchers.actions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class ActionTypeRegistry {
    private final Map<String, ActionType> actionTypes = new HashMap<>();

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    public ActionTypeRegistry(Collection<ActionType> standardActionTypes) {
        for (ActionType actionType : standardActionTypes) {
            register(actionType);
        }
    }

    public Optional<ActionType> getByTag(String tag) {
        return Optional.ofNullable(actionTypes.get(tag));
    }

    public void register(ActionType actionType) {
        if (actionTypes.put(actionType.getTag(), actionType) != null) {
            logger.warning(() ->
                    "Multiple action types registered with tag " + actionType.getTag() + ".");
        }
    }

    public void unregister(ActionType actionType) {
        actionTypes.remove(actionType.getTag());
    }

}
