package ru.smartech.app.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.time.Duration;
import java.util.function.Function;

public class Caches {

    public static <Key, Val> LoadingCache<Key, Val> simpleCache(Function<Key, Val> loader, int cacheSize, Duration cacheDuration) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(cacheDuration)
                .maximumSize(cacheSize)
                .build(new CacheLoader<Key, Val>() {
                    @Override
                    public Val load(Key key) {
                        return loader.apply(key);
                    }
                });
    }
}
