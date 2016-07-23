package com.geoffslittle.datastructure.generalizedmap;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class InvolutionTest {

    private Involution<Object> involution;

    @Before
    public void before() {
        involution = Involution.newInvolution();
    }

    @Test(expected = IllegalStateException.class)
    public void cantAddFixedPoint() {
        Object o1 = new Object();

        involution.put(o1, Optional.of(o1));
    }

    @Test
    public void addsInverse() {
        Object o1 = new Object();
        Object o2 = new Object();

        involution.put(o1, Optional.of(o2));

        assertEquals(involution.get(o1), Optional.of(o2));
        assertEquals(involution.get(o2), Optional.of(o1));
    }

}