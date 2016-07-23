package com.geoffslittle.datastructure.generalizedmap;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SafeMapTest {

    private SafeMap<Object, Object> safeMap;

    @Before
    public void before() {
        safeMap = SafeMap.newSafeMap();
    }

    @Test
    public void containsWrappedAndUnwrapped() {
        Object o1 = new Object();
        Object o2 = new Object();
        safeMap.put(o1, Optional.of(o2));

        assertTrue(safeMap.containsValue(Optional.of(o2)));
        assertTrue(safeMap.containsValue(o2));
    }

    @Test
    public void canGetExisting() {
        Object o1 = new Object();
        Object o2 = new Object();
        safeMap.put(o1, Optional.of(o2));

        assertEquals(Optional.of(o2), safeMap.get(o1));
    }

    @Test
    public void noPutGetsEmpty() {
        assertEquals(Optional.empty(), safeMap.get(new Object()));
    }

    @Test
    public void putEmptyGetsEmpty() {
        Object o1 = new Object();
        safeMap.put(o1, Optional.empty());

        assertEquals(Optional.empty(), safeMap.get(o1));
    }

    @Test
    public void putNullGetsEmpty() {
        Object o1 = new Object();
        safeMap.put(o1, null);

        assertEquals(Optional.empty(), safeMap.get(o1));
    }

}