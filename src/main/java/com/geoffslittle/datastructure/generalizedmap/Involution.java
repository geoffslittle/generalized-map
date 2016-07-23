package com.geoffslittle.datastructure.generalizedmap;

import com.google.common.base.Preconditions;

import java.util.AbstractMap;
import java.util.Optional;
import java.util.Set;

public class Involution<E> extends AbstractMap<E, Optional<E>>  {

    private final SafeMap<E, E> safeMap;

    private Involution() {
        this.safeMap = SafeMap.newSafeMap();
    }

    public static <E> Involution<E> newInvolution() {
        return new Involution<E>();
    }

    @Override
    public Set<Entry<E, Optional<E>>> entrySet() {
        return safeMap.entrySet();
    }

    @Override
    public Optional<E> put(E key, Optional<E> value) {
        // Prohibit fixed points
        value.ifPresent(valueGuts -> Preconditions.checkState(!key.equals(valueGuts),
                "Cannot add mapping from element to same element"));

        // Add inverse
        value.ifPresent(valueGuts -> safeMap.put(valueGuts, Optional.of(key)));
        return safeMap.put(key, value);
    }

    @Override
    public Optional<E> remove(Object key) {
        // Remove inverse
        safeMap.get(key).ifPresent(coelementGuts -> safeMap.remove(coelementGuts));
        return safeMap.remove(key);
    }

}
