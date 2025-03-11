package com.springboot.lib.sm;

public abstract class Action<T extends SMData<T>> {

    private final String key;
    private final State<T> next;

    public Action(String key, State<T> nextState) {
        this.next = nextState;
        this.key = key;
    }

    public State<T> next() {
        return this.next;
    }

    public String key() {
        return this.key;
    }

    protected abstract void doAction(T data);
}
