package com.springboot.lib.sm;

public class ManualAction<T extends SMData<T>> extends Action<T>{
    public ManualAction(String key, State<T> nextState) {
        super(key, nextState);
    }

    @Override
    protected void doAction(T data) {

    }
}
