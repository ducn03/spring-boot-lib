package com.springboot.lib.sm;

import lombok.Getter;

@Getter
public abstract class Template<T extends SMData<T>> {


    private State<T>[] states;
    @Getter
    private final String id;

    protected Template(String id) {
        this.id = id;
    }

    @SafeVarargs
    protected final void map(State<T>... states) {
        this.states = states;
    }

    State<T> getState(String key) {
        if (this.states == null) {
            return null;
        }
        for (State<T> state : this.states) {
            if (state.getKey().equals(key)) {
                return state;
            }
        }
        return null;
    }

    public State<T> getDefaultInitState() {
        return this.states[0];
    }
}
