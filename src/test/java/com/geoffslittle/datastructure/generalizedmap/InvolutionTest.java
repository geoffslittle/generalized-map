package com.geoffslittle.datastructure.generalizedmap;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class InvolutionTest {

    private Involution<Object> involution;

    @Before
    public void before() {
        involution = Involution.newInvolution();
    }

    @Test
    public void canAddAndMappingToEmpty() {
        Object element = new Object();

        involution.addMapping(element, null);

        assertTrue(involution.mappings().containsKey(element));
        assertNull(involution.mappings().get(element));
    }

    @Test
    public void canAddMappingToDifferentElement() {
        Object element1 = new Object();
        Object element2 = new Object();

        involution.addMapping(element1, element2);

        assertTrue(involution.mappings().containsKey(element1));
        assertTrue(involution.mappings().containsKey(element2));
        assertEquals(involution.mappings().get(element1), element2);
        assertEquals(involution.mappings().get(element2), element1);
    }

    @Test
    public void canAddMappingToEmptyThenToDifferentElement() {
        Object element1 = new Object();
        Object element2 = new Object();

        involution.addMapping(element1, null);
        involution.addMapping(element1, element2);

        assertTrue(involution.mappings().containsKey(element1));
        assertTrue(involution.mappings().containsKey(element2));
        assertEquals(involution.mappings().get(element1), element2);
        assertEquals(involution.mappings().get(element2), element1);
    }

    @Test(expected = IllegalStateException.class)
    public void cantAddFixedPoint() {
        Object element1 = new Object();

        involution.addMapping(element1, element1);
    }

    @Test(expected = IllegalStateException.class)
    public void cantModifyExistingMapping() {
        Object element1 = new Object();
        Object element2 = new Object();

        involution.addMapping(element1, element2);
        involution.addMapping(element1, null);
    }

    @Test
    public void canRemoveMapping() {
        Object element1 = new Object();
        Object element2 = new Object();

        involution.addMapping(element1, element2);
        involution.removeMapping(element1);

        assertFalse(involution.mappings().containsKey(element1));
        assertFalse(involution.mappings().containsKey(element2));
    }

    @Test(expected = IllegalStateException.class)
    public void cantRemoveNonExistingMapping() {
        involution.removeMapping(new Object());
    }

}