package com.springboot.lib.cache;

public interface ILazyCache<T extends CacheData> {

    public T get();
    public T load();
}
