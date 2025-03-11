package com.springboot.lib.sm;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class State<T extends SMData<T>> {

    private final String key;
    private final List<Action<T>> actions = new ArrayList<>();
    private final String DEFAULT_ACTION_STATUS = "NONE";

    public State(String key) {
        this.key = key;
    }

    public void action(Action<T> action) {
        this.actions.add(action);
    }

    Action<T> getAction(String key) {
        if (key == null) {
            // check and send default action
            if (this.actions.size() == 1) {
                return this.actions.get(0);
            }
            return null;
        }
        for (Action<T> action : this.actions) {
            if (action.key().equals(key)) {
                return action;
            }
        }
        return null;
    }

    public String getDefaultActionStatus() {
        return this.DEFAULT_ACTION_STATUS;
    }
}
