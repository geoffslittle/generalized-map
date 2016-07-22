package com.geoffslittle.datastructure.generalizedmap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

/*
 * This is a partial involution without fixed point.
 */
public class Involution<E> {

    private final Map<E, E> mappings;

    private Involution() {
        this.mappings = Maps.newHashMap();
    }

    public static <E> Involution<E> newInvolution() {
        return new Involution<E>();
    }

    public Map<E, E> mappings() {
        return Collections.unmodifiableMap(mappings);
    }

    public void addMapping(@NonNull E element, E coelement) {
        assertElementHasNoCoelement(element);
        // Mapping an element to the empty set (null) is allowed
        if (coelement != null) {
            // Fixed point not allowed
            Preconditions.checkState(!element.equals(coelement), "Cannot add mapping from element to same element");
            assertElementDoesntExist(coelement);

            mappings.put(coelement, element);
        }
        mappings.put(element, coelement);
    }

    public void removeMapping(@NonNull E element) {
        assertElementExists(element);
        E coelement = mappings.get(element);
        // This element may be mapped to the empty set
        if (coelement != null) {
            // We can assume that since the element is mapped to the coelement, that the reverse is also true
            mappings.remove(coelement);
        }
        mappings.remove(element);
    }

    public Involution inverse() {
        Involution inverse = Involution.newInvolution();
        for (Map.Entry<E, E> mapping : mappings.entrySet()) {
            if (mapping.getValue() != null) {
                inverse.addMapping(mapping.getValue(), mapping.getKey());
            }
        }
        return inverse;
    }

    private void assertElementExists(@NonNull E element) {
        Preconditions.checkState(mappings.containsKey(element), "Element doesn't exist");
    }

    private void assertElementDoesntExist(@NonNull E element) {
        Preconditions.checkState(!mappings.containsKey(element), "Element already exists");
    }

    private void assertElementHasNoCoelement(E element) {
        Preconditions.checkState(mappings.get(element) == null, "This element has a coelement");
    }

    private void assertInverse(@NonNull E element, @NonNull E coelement) {
        Preconditions.checkState(mappings.get(element).equals(coelement), "This element doesn't map to the coelement");
        Preconditions.checkState(mappings.get(coelement).equals(element),
                "This coelement doesn't map to the element");
    }

}
