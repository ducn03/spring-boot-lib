package com.springboot.lib.sm;

public interface ITrigger<T extends SMData> {

    T getData();
}
