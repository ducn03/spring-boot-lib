package com.springboot.lib.cache;

public abstract class LazyCache<T extends CacheData> implements ILazyCache<T> {

    private long time;
    private final int delay;

    private T data;

    public LazyCache(int delay) {
        this.delay = delay;
    }
    @Override
    public T get() {
        long time = System.currentTimeMillis();
        if (this.time <= time) {
            this.data = this.load();
            this.time = time + this.delay;
        }
        return this.data;
    }

    public void clear() {
        this.data = null;
        this.time = 0;
    }
}
