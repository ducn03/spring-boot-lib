package com.springboot.lib.cache;

public interface ILazyCache<T> {

    public T get();
    public T load();
}
