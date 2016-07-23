package com.geoffslittle.datastructure.generalizedmap;

import com.google.common.collect.Maps;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class SafeMap<K, V> extends AbstractMap<K, Optional<V>>  implements Map<K, Optional<V>> {

    private final Map<K, Optional<V>> unsafeMap;

    private SafeMap() {
        this.unsafeMap = Maps.newHashMap();
    }

    public static <K, V> SafeMap<K, V> newSafeMap() {
        return new SafeMap<K, V>();
    }

    // Allow value membership check using wrapped or unwrapped value
    @Override
    public boolean containsValue(Object value) {
        if (value instanceof Optional) {
            return unsafeMap.containsValue(value);
        }
        return unsafeMap.containsValue(Optional.ofNullable(value));
    }

    @Override
    public Set<Entry<K, Optional<V>>> entrySet() {
        return unsafeMap.entrySet();
    }

    @Override
    public Optional<V> get(Object key) {
        return Optional.ofNullable(unsafeMap.get(key)).flatMap(Function.identity());
    }

    @Override
    public Optional<V> put(K key, Optional<V> value) {
        return unsafeMap.put(key, Optional.ofNullable(value).flatMap(Function.identity()));
    }

}
