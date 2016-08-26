package com.geoffslittle.datastructure.generalizedmap;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NGMapTest {

    @Test
    public void noArgsConstructorIs0D() {
        NGMap ngMap = NGMap.ngMap();

        assertEquals(0, ngMap.dimension());
    }

    @Test(expected = IllegalStateException.class)
    public void zeroGMapCantDecreaseDimension() {
        NGMap ngMap = NGMap.ngMap();

        ngMap.decreaseDimension();
    }

    @Test
    public void increaseDimensionToNPlusOne() {
        NGMap ngMap = NGMap.ngMap();

        ngMap.increaseDimension();

        assertEquals(1, ngMap.dimension());
    }

    @Test
    public void decreaseDimensionToNMinusOne() {
        NGMap ngMap = NGMap.ngMap();
        ngMap.increaseDimension();

        ngMap.decreaseDimension();
        assertEquals(0, ngMap.dimension());
    }

    @Test
    public void constructNGMapGivenN() {
        NGMap ngMap = NGMap.ngMap(5);

        assertEquals(5, ngMap.dimension());
    }

    @Test
    public void lessThan0NCreates0GMap() {
        NGMap ngMap = NGMap.ngMap(-1);

        assertEquals(0, ngMap.dimension());
    }

    @Test
    public void canAddIsolatedDart() {
        NGMap ngMap = NGMap.ngMap();

        NGMap.Dart isolatedDart = ngMap.addIsolatedDart();

        assertTrue(ngMap.containsDart(isolatedDart));
        assertTrue(ngMap.isIsolated(isolatedDart));
    }

    @Test
    public void canRemoveIsolatedDart() {
        NGMap ngMap = NGMap.ngMap();

        NGMap.Dart isolatedDart = ngMap.addIsolatedDart();
        ngMap.removeIsolatedDart(isolatedDart);

        assertFalse(ngMap.containsDart(isolatedDart));
        assertTrue(ngMap.isIsolated(isolatedDart));
    }

    @Test
    public void removingNonExistentDartAllowed() {
        NGMap ngMap = NGMap.ngMap();
        NGMap.Dart nonExistentDart = ngMap.addIsolatedDart();

        ngMap.removeIsolatedDart(nonExistentDart);
        ngMap.removeIsolatedDart(nonExistentDart);

        assertFalse(ngMap.containsDart(nonExistentDart));
        assertTrue(ngMap.isIsolated(nonExistentDart));
    }

    @Test
    public void twoIsolatedDartsAreSewable() {
        NGMap ngMap = NGMap.ngMap();
        NGMap.Dart leftDart = ngMap.addIsolatedDart();
        NGMap.Dart rightDart = ngMap.addIsolatedDart();

        assertTrue(ngMap.isSewable(leftDart, rightDart, 0));
    }

    @Test
    public void sewTwoDartsNonIsolatedAndReachable() {
        NGMap ngMap = NGMap.ngMap();
        NGMap.Dart leftDart = ngMap.addIsolatedDart();
        NGMap.Dart rightDart = ngMap.addIsolatedDart();

        ngMap.sew(leftDart, rightDart, 0);

        assertFalse(ngMap.isIsolated(leftDart));
        assertFalse(ngMap.isIsolated(rightDart));

        Iterator<NGMap.Dart> it = ngMap.genericIterator(leftDart, Lists.newArrayList(0));
        assertTrue(it.hasNext());
        assertEquals(leftDart, it.next());
        assertTrue(it.hasNext());
        assertEquals(rightDart, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void unsewTwoDarts() {
        NGMap ngMap = NGMap.ngMap();
        NGMap.Dart leftDart = ngMap.addIsolatedDart();
        NGMap.Dart rightDart = ngMap.addIsolatedDart();

        ngMap.sew(leftDart, rightDart, 0);
        ngMap.unsew(leftDart, 0);

        assertTrue(ngMap.isIsolated(leftDart));
        assertTrue(ngMap.isIsolated(rightDart));

        Iterator<NGMap.Dart> leftIt = ngMap.genericIterator(leftDart, Lists.newArrayList(0));
        assertTrue(leftIt.hasNext());
        assertEquals(leftDart, leftIt.next());
        assertFalse(leftIt.hasNext());

        Iterator<NGMap.Dart> rightIt = ngMap.genericIterator(rightDart, Lists.newArrayList(0));
        assertTrue(rightIt.hasNext());
        assertEquals(rightDart, rightIt.next());
        assertFalse(rightIt.hasNext());
    }

    @Test
    public void create1DManifoldWith3VerticesAnd2Edges() {
        NGMap ngMap = NGMap.ngMap(1);
        NGMap.Dart dart1 = ngMap.addIsolatedDart();
        NGMap.Dart dart2 = ngMap.addIsolatedDart();
        NGMap.Dart dart3 = ngMap.addIsolatedDart();
        NGMap.Dart dart4 = ngMap.addIsolatedDart();

        ngMap.sew(dart1, dart2, 0);
        ngMap.sew(dart3, dart4, 0);
        ngMap.sew(dart2, dart3, 1);

        Set<NGMap.Dart> vertex1 = Sets.newHashSet(ngMap.genericIterator(dart1, Lists.newArrayList(1)));
        assertEquals(Sets.newHashSet(dart1), vertex1);

        Set<NGMap.Dart> vertex2 = Sets.newHashSet(ngMap.genericIterator(dart2, Lists.newArrayList(1)));
        assertEquals(Sets.newHashSet(dart2, dart3), vertex2);

        Set<NGMap.Dart> vertex3 = Sets.newHashSet(ngMap.genericIterator(dart4, Lists.newArrayList(1)));
        assertEquals(Sets.newHashSet(dart4), vertex3);

        Set<NGMap.Dart> edge1 = Sets.newHashSet(ngMap.genericIterator(dart1, Lists.newArrayList(0)));
        assertEquals(Sets.newHashSet(dart1, dart2), edge1);

        Set<NGMap.Dart> edge2 = Sets.newHashSet(ngMap.genericIterator(dart3, Lists.newArrayList(0)));
        assertEquals(Sets.newHashSet(dart3, dart4), edge2);
    }

    @Test
    public void create2DManifoldWith4Vertices5EdgesAnd2Faces() {
        NGMap ngMap = NGMap.ngMap(2);
        NGMap.Dart dart1 = ngMap.addIsolatedDart();
        NGMap.Dart dart2 = ngMap.addIsolatedDart();
        NGMap.Dart dart3 = ngMap.addIsolatedDart();
        NGMap.Dart dart4 = ngMap.addIsolatedDart();
        NGMap.Dart dart5 = ngMap.addIsolatedDart();
        NGMap.Dart dart6 = ngMap.addIsolatedDart();
        NGMap.Dart dart7 = ngMap.addIsolatedDart();
        NGMap.Dart dart8 = ngMap.addIsolatedDart();
        NGMap.Dart dart9 = ngMap.addIsolatedDart();
        NGMap.Dart dart10 = ngMap.addIsolatedDart();
        NGMap.Dart dart11 = ngMap.addIsolatedDart();
        NGMap.Dart dart12 = ngMap.addIsolatedDart();

        // a0 relationships
        // face 1
        ngMap.sew(dart1, dart2, 0);
        ngMap.sew(dart3, dart4, 0);
        ngMap.sew(dart5, dart6, 0);
        // face 2
        ngMap.sew(dart7, dart8, 0);
        ngMap.sew(dart9, dart10, 0);
        ngMap.sew(dart11, dart12, 0);

        // a1 relationships
        // face 1
        ngMap.sew(dart1, dart6, 1);
        ngMap.sew(dart2, dart3, 1);
        ngMap.sew(dart4, dart5, 1);
        // face 2
        ngMap.sew(dart7, dart12, 1);
        ngMap.sew(dart8, dart9, 1);
        ngMap.sew(dart10, dart11, 1);

        // a2 relationships
        ngMap.sew(dart1, dart7, 2);
//        ngMap.sew(dart2, dart8, 2);


        // Validate vertices 1-4
        Set<NGMap.Dart> vertex1 = Sets.newHashSet(ngMap.genericIterator(dart1, Lists.newArrayList(1, 2)));
        assertEquals(Sets.newHashSet(dart1, dart6, dart7, dart12), vertex1);

        Set<NGMap.Dart> vertex2 = Sets.newHashSet(ngMap.genericIterator(dart2, Lists.newArrayList(1, 2)));
        assertEquals(Sets.newHashSet(dart2, dart3, dart8, dart9), vertex2);

        Set<NGMap.Dart> vertex3 = Sets.newHashSet(ngMap.genericIterator(dart4, Lists.newArrayList(1, 2)));
        assertEquals(Sets.newHashSet(dart4, dart5), vertex3);

        Set<NGMap.Dart> vertex4 = Sets.newHashSet(ngMap.genericIterator(dart10, Lists.newArrayList(1, 2)));
        assertEquals(Sets.newHashSet(dart10, dart11), vertex4);

        // Validate edges 1-5
        Set<NGMap.Dart> edge1 = Sets.newHashSet(ngMap.genericIterator(dart1, Lists.newArrayList(0, 2)));
        assertEquals(Sets.newHashSet(dart1, dart2, dart7, dart8), edge1);

        Set<NGMap.Dart> edge2 = Sets.newHashSet(ngMap.genericIterator(dart3, Lists.newArrayList(0, 2)));
        assertEquals(Sets.newHashSet(dart3, dart4), edge2);

        Set<NGMap.Dart> edge3 = Sets.newHashSet(ngMap.genericIterator(dart5, Lists.newArrayList(0, 2)));
        assertEquals(Sets.newHashSet(dart5, dart6), edge3);

        Set<NGMap.Dart> edge4 = Sets.newHashSet(ngMap.genericIterator(dart9, Lists.newArrayList(0, 2)));
        assertEquals(Sets.newHashSet(dart9, dart10), edge4);

        Set<NGMap.Dart> edge5 = Sets.newHashSet(ngMap.genericIterator(dart11, Lists.newArrayList(0, 2)));
        assertEquals(Sets.newHashSet(dart11, dart12), edge5);

        // Validate faces 1-2
        Set<NGMap.Dart> face1 = Sets.newHashSet(ngMap.genericIterator(dart1, Lists.newArrayList(0, 1)));
        assertEquals(Sets.newHashSet(dart1, dart2, dart3, dart4, dart5, dart6), face1);

        Set<NGMap.Dart> face2 = Sets.newHashSet(ngMap.genericIterator(dart7, Lists.newArrayList(0, 1)));
        assertEquals(Sets.newHashSet(dart7, dart8, dart9, dart10, dart11, dart12), face2);
    }

    @Test
    public void cubeHas8Vertices12Edges6Faces() {
        NGMap ngMap = NGMap.ngMap(3);
        Cube cube = addCube(ngMap);

        // 8 vertices
        Set<NGMap.Dart> vertex1 = Sets.newHashSet(ngMap.genericIterator(cube._1._1._1, Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(cube._1._1._1, cube._1._4._2, cube._2._3._2, cube._2._4._1, cube._5._1._2,
                cube._5._2._1), vertex1);

        Set<NGMap.Dart> vertex2 = Sets.newHashSet(ngMap.genericIterator(cube._1._1._2, Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(cube._1._1._2, cube._1._2._1, cube._2._2._2, cube._2._3._1, cube._3._1._1,
                cube._3._4._2), vertex2);

        Set<NGMap.Dart> vertex3 = Sets.newHashSet(ngMap.genericIterator(cube._1._2._2, Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(cube._1._2._2, cube._1._3._1, cube._3._4._1, cube._3._3._2, cube._4._1._2,
                cube._4._2._1), vertex3);

        Set<NGMap.Dart> vertex4 = Sets.newHashSet(ngMap.genericIterator(cube._1._3._2, Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(cube._1._3._2, cube._1._4._1, cube._4._1._1, cube._4._4._2, cube._5._2._2,
                cube._5._3._1), vertex4);

        Set<NGMap.Dart> vertex5 = Sets.newHashSet(ngMap.genericIterator(cube._2._1._1, Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(cube._2._1._1, cube._2._4._2, cube._5._1._1, cube._5._4._2, cube._6._3._2,
                cube._6._4._1), vertex5);

        Set<NGMap.Dart> vertex6 = Sets.newHashSet(ngMap.genericIterator(cube._2._1._2, Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(cube._2._1._2, cube._2._2._1, cube._3._1._2, cube._3._2._1, cube._6._2._2,
                cube._6._3._1), vertex6);

        Set<NGMap.Dart> vertex7 = Sets.newHashSet(ngMap.genericIterator(cube._4._2._2, Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(cube._4._2._2, cube._4._3._1, cube._3._2._2, cube._3._3._1, cube._6._1._2,
                cube._6._2._1), vertex7);

        Set<NGMap.Dart> vertex8 = Sets.newHashSet(ngMap.genericIterator(cube._4._3._2, Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(cube._4._3._2, cube._4._4._1, cube._5._3._2, cube._5._4._1, cube._6._1._1,
                cube._6._4._2), vertex8);

        // 12 edges
        Set<NGMap.Dart> edge1 = Sets.newHashSet(ngMap.genericIterator(cube._1._1._1, Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(cube._1._1._1, cube._1._1._2, cube._2._3._1, cube._2._3._2), edge1);

        Set<NGMap.Dart> edge2 = Sets.newHashSet(ngMap.genericIterator(cube._1._2._1, Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(cube._1._2._1, cube._1._2._2, cube._3._4._1, cube._3._4._2), edge2);

        Set<NGMap.Dart> edge3 = Sets.newHashSet(ngMap.genericIterator(cube._1._3._1, Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(cube._1._3._1, cube._1._3._2, cube._4._1._1, cube._4._1._2), edge3);

        Set<NGMap.Dart> edge4 = Sets.newHashSet(ngMap.genericIterator(cube._1._4._1, Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(cube._1._4._1, cube._1._4._2, cube._5._2._1, cube._5._2._2), edge4);

        Set<NGMap.Dart> edge5 = Sets.newHashSet(ngMap.genericIterator(cube._2._4._1, Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(cube._2._4._1, cube._2._4._2, cube._5._1._1, cube._5._1._2), edge5);

        Set<NGMap.Dart> edge6 = Sets.newHashSet(ngMap.genericIterator(cube._2._2._1, Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(cube._2._2._1, cube._2._2._2, cube._3._1._1, cube._3._1._2), edge6);

        Set<NGMap.Dart> edge7 = Sets.newHashSet(ngMap.genericIterator(cube._3._3._1, Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(cube._3._3._1, cube._3._3._2, cube._4._2._1, cube._4._2._2), edge7);

        Set<NGMap.Dart> edge8 = Sets.newHashSet(ngMap.genericIterator(cube._4._4._1, Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(cube._4._4._1, cube._4._4._2, cube._5._3._1, cube._5._3._2), edge8);

        // 6 faces
        Set<NGMap.Dart> face1 = Sets.newHashSet(ngMap.genericIterator(cube._1._1._1, Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(cube._1._1._1, cube._1._1._2, cube._1._2._1, cube._1._2._2, cube._1._3._1,
                cube._1._3._2, cube._1._4._1, cube._1._4._2), face1);

        Set<NGMap.Dart> face2 = Sets.newHashSet(ngMap.genericIterator(cube._2._1._1, Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(cube._2._1._1, cube._2._1._2, cube._2._2._1, cube._2._2._2, cube._2._3._1,
                cube._2._3._2, cube._2._4._1, cube._2._4._2), face2);

        Set<NGMap.Dart> face3 = Sets.newHashSet(ngMap.genericIterator(cube._3._1._1, Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(cube._3._1._1, cube._3._1._2, cube._3._2._1, cube._3._2._2, cube._3._3._1,
                cube._3._3._2, cube._3._4._1, cube._3._4._2), face3);

        Set<NGMap.Dart> face4 = Sets.newHashSet(ngMap.genericIterator(cube._4._1._1, Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(cube._4._1._1, cube._4._1._2, cube._4._2._1, cube._4._2._2, cube._4._3._1,
                cube._4._3._2, cube._4._4._1, cube._4._4._2), face4);

        Set<NGMap.Dart> face5 = Sets.newHashSet(ngMap.genericIterator(cube._5._1._1, Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(cube._5._1._1, cube._5._1._2, cube._5._2._1, cube._5._2._2, cube._5._3._1,
                cube._5._3._2, cube._5._4._1, cube._5._4._2), face5);

        Set<NGMap.Dart> face6 = Sets.newHashSet(ngMap.genericIterator(cube._6._1._1, Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(cube._6._1._1, cube._6._1._2, cube._6._2._1, cube._6._2._2, cube._6._3._1,
                cube._6._3._2, cube._6._4._1, cube._6._4._2), face6);

    }

    @Test
    public void pyramidHas5Vertices8Edges5Faces() {
        NGMap ngMap = NGMap.ngMap(3);
        Pyramid pyramid = addPyramid(ngMap);

        // 5 vertices
        Set<NGMap.Dart> vertex1 = Sets.newHashSet(ngMap.genericIterator(pyramid.square._1._1,
                Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(pyramid.square._1._1, pyramid.square._4._2, pyramid.triangle1._1._1,
                pyramid.triangle1._3._2, pyramid.triangle4._1._2, pyramid.triangle4._2._1), vertex1);

        Set<NGMap.Dart> vertex2 = Sets.newHashSet(ngMap.genericIterator(pyramid.square._1._2,
                Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(pyramid.square._1._2, pyramid.square._2._1, pyramid.triangle1._2._2,
                pyramid.triangle1._3._1, pyramid.triangle2._1._2, pyramid.triangle2._2._1), vertex2);

        Set<NGMap.Dart> vertex3 = Sets.newHashSet(ngMap.genericIterator(pyramid.square._2._2,
                Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(pyramid.square._2._2, pyramid.square._3._1, pyramid.triangle2._1._1,
                pyramid.triangle2._3._2, pyramid.triangle3._1._2, pyramid.triangle3._2._1), vertex3);

        Set<NGMap.Dart> vertex4 = Sets.newHashSet(ngMap.genericIterator(pyramid.square._3._2,
                Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(pyramid.square._3._2, pyramid.square._4._1, pyramid.triangle3._1._1,
                pyramid.triangle3._3._2, pyramid.triangle4._2._2, pyramid.triangle4._3._1), vertex4);

        Set<NGMap.Dart> vertex5 = Sets.newHashSet(ngMap.genericIterator(pyramid.triangle1._1._2,
                Lists.newArrayList(1, 2, 3)));
        assertEquals(Sets.newHashSet(pyramid.triangle1._1._2, pyramid.triangle1._2._1, pyramid.triangle2._2._2,
                pyramid.triangle2._3._1, pyramid.triangle3._2._2, pyramid.triangle3._3._1, pyramid.triangle4._1._1,
                pyramid.triangle4._3._2), vertex5);

        // 8 edges
        Set<NGMap.Dart> edge1 = Sets.newHashSet(ngMap.genericIterator(pyramid.square._1._1,
                Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(pyramid.square._1._1, pyramid.square._1._2, pyramid.triangle1._3._1,
                pyramid.triangle1._3._2), edge1);

        Set<NGMap.Dart> edge2 = Sets.newHashSet(ngMap.genericIterator(pyramid.square._2._1,
                Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(pyramid.square._2._1, pyramid.square._2._2, pyramid.triangle2._1._1,
                pyramid.triangle2._1._2), edge2);

        Set<NGMap.Dart> edge3 = Sets.newHashSet(ngMap.genericIterator(pyramid.square._3._1,
                Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(pyramid.square._3._1, pyramid.square._3._2, pyramid.triangle3._1._1,
                pyramid.triangle3._1._2), edge3);

        Set<NGMap.Dart> edge4 = Sets.newHashSet(ngMap.genericIterator(pyramid.square._4._1,
                Lists.newArrayList(0, 2, 3)));
        assertEquals(Sets.newHashSet(pyramid.square._4._1, pyramid.square._4._2, pyramid.triangle4._2._1,
                pyramid.triangle4._2._2), edge4);

        // 5 faces
        Set<NGMap.Dart> face1 = Sets.newHashSet(ngMap.genericIterator(pyramid.square._1._1,
                Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(pyramid.square._1._1, pyramid.square._1._2, pyramid.square._2._1,
                pyramid.square._2._2, pyramid.square._3._1, pyramid.square._3._2, pyramid.square._4._1,
                pyramid.square._4._2), face1);

        Set<NGMap.Dart> face2 = Sets.newHashSet(ngMap.genericIterator(pyramid.triangle1._1._1,
                Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(pyramid.triangle1._1._1, pyramid.triangle1._1._2, pyramid.triangle1._2._1,
                pyramid.triangle1._2._2, pyramid.triangle1._3._1, pyramid.triangle1._3._2), face2);

        Set<NGMap.Dart> face3 = Sets.newHashSet(ngMap.genericIterator(pyramid.triangle2._1._1,
                Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(pyramid.triangle2._1._1, pyramid.triangle2._1._2, pyramid.triangle2._2._1,
                pyramid.triangle2._2._2, pyramid.triangle2._3._1, pyramid.triangle2._3._2), face3);

        Set<NGMap.Dart> face4 = Sets.newHashSet(ngMap.genericIterator(pyramid.triangle3._1._1,
                Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(pyramid.triangle3._1._1, pyramid.triangle3._1._2, pyramid.triangle3._2._1,
                pyramid.triangle3._2._2, pyramid.triangle3._3._1, pyramid.triangle3._3._2), face4);

        Set<NGMap.Dart> face5 = Sets.newHashSet(ngMap.genericIterator(pyramid.triangle4._1._1,
                Lists.newArrayList(0, 1, 3)));
        assertEquals(Sets.newHashSet(pyramid.triangle4._1._1, pyramid.triangle4._1._2, pyramid.triangle4._2._1,
                pyramid.triangle4._2._2, pyramid.triangle4._3._1, pyramid.triangle4._3._2), face5);
    }

    @Test
    public void houseIsCubePlusPyramidVia4Vertices4Edges2Faces() {
        NGMap ngMap = NGMap.ngMap(3);
        House house = addHouse(ngMap);

        // We assert that vertices, edges, and faces are valid for the cube and pyramid independently, here we assert
        // validity of the vertices, edges, and faces of both volumes, i.e. the intersection

        // 4 vertices
        Set<NGMap.Dart> vertex1 = Sets.newHashSet(ngMap.genericIterator(house.cube._1._1._1,
                Lists.newArrayList(1, 2, 3)));
        assertEquals(ImmutableSet.builder().addAll(ngMap.genericIterator(house.cube._1._1._1,
                Lists.newArrayList(1, 2))).addAll(ngMap.genericIterator(house.pyramid.square._1._1,
                Lists.newArrayList(1, 2))).build(), vertex1);

        Set<NGMap.Dart> vertex2 = Sets.newHashSet(ngMap.genericIterator(house.cube._1._1._2,
                Lists.newArrayList(1, 2, 3)));
        assertEquals(ImmutableSet.builder().addAll(ngMap.genericIterator(house.cube._1._1._2,
                Lists.newArrayList(1, 2))).addAll(ngMap.genericIterator(house.pyramid.square._1._2,
                Lists.newArrayList(1, 2))).build(), vertex2);

        Set<NGMap.Dart> vertex3 = Sets.newHashSet(ngMap.genericIterator(house.cube._1._2._1,
                Lists.newArrayList(1, 2, 3)));
        assertEquals(ImmutableSet.builder().addAll(ngMap.genericIterator(house.cube._1._2._1,
                Lists.newArrayList(1, 2))).addAll(ngMap.genericIterator(house.pyramid.square._2._1,
                Lists.newArrayList(1, 2))).build(), vertex3);

        Set<NGMap.Dart> vertex4 = Sets.newHashSet(ngMap.genericIterator(house.cube._1._2._2,
                Lists.newArrayList(1, 2, 3)));
        assertEquals(ImmutableSet.builder().addAll(ngMap.genericIterator(house.cube._1._2._2,
                Lists.newArrayList(1, 2))).addAll(ngMap.genericIterator(house.pyramid.square._2._2,
                Lists.newArrayList(1, 2))).build(), vertex4);

        // 4 edges
        Set<NGMap.Dart> edge1 = Sets.newHashSet(ngMap.genericIterator(house.cube._1._1._1,
                Lists.newArrayList(0, 2, 3)));
        assertEquals(ImmutableSet.builder().addAll(ngMap.genericIterator(house.cube._1._1._1,
                Lists.newArrayList(0, 2))).addAll(ngMap.genericIterator(house.pyramid.square._1._1,
                Lists.newArrayList(0, 2))).build(), edge1);

        Set<NGMap.Dart> edge2 = Sets.newHashSet(ngMap.genericIterator(house.cube._1._2._1,
                Lists.newArrayList(0, 2, 3)));
        assertEquals(ImmutableSet.builder().addAll(ngMap.genericIterator(house.cube._1._2._1,
                Lists.newArrayList(0, 2))).addAll(ngMap.genericIterator(house.pyramid.square._2._1,
                Lists.newArrayList(0, 2))).build(), edge2);

        Set<NGMap.Dart> edge3 = Sets.newHashSet(ngMap.genericIterator(house.cube._1._3._1,
                Lists.newArrayList(0, 2, 3)));
        assertEquals(ImmutableSet.builder().addAll(ngMap.genericIterator(house.cube._1._3._1,
                Lists.newArrayList(0, 2))).addAll(ngMap.genericIterator(house.pyramid.square._3._1,
                Lists.newArrayList(0, 2))).build(), edge3);

        Set<NGMap.Dart> edge4 = Sets.newHashSet(ngMap.genericIterator(house.cube._1._4._1,
                Lists.newArrayList(0, 2, 3)));
        assertEquals(ImmutableSet.builder().addAll(ngMap.genericIterator(house.cube._1._4._1,
                Lists.newArrayList(0, 2))).addAll(ngMap.genericIterator(house.pyramid.square._4._1,
                Lists.newArrayList(0, 2))).build(), edge4);

        // 1 face
        Set<NGMap.Dart> face1 = Sets.newHashSet(ngMap.genericIterator(house.cube._1._1._1,
                Lists.newArrayList(0, 1, 3)));
        assertEquals(ImmutableSet.builder().addAll(ngMap.genericIterator(house.cube._1._1._1,
                Lists.newArrayList(0, 1))).addAll(ngMap.genericIterator(house.pyramid.square._1._1,
                Lists.newArrayList(0, 1))).build(), face1);
    }

    @Test
    public void canMakeACylinder() {
        NGMap ngMap = NGMap.ngMap(2);
        Square square = addSquare(ngMap);

        ngMap.sew(square._2._1, square._4._2, 2);

        // 2 vertices
        Set<NGMap.Dart> vertex1 = Sets.newHashSet(ngMap.genericIterator(square._1._1,
                Lists.newArrayList(1, 2)));
        assertEquals(Sets.newHashSet(square._1._1, square._4._2, square._1._2, square._2._1), vertex1);

        Set<NGMap.Dart> vertex2 = Sets.newHashSet(ngMap.genericIterator(square._3._1,
                Lists.newArrayList(1, 2)));
        assertEquals(Sets.newHashSet(square._3._1, square._2._2, square._3._2, square._4._1), vertex2);

        // Only 1 face with all the darts
        Set<NGMap.Dart> face1 = Sets.newHashSet(ngMap.genericIterator(square._1._1,
                Lists.newArrayList(0, 1)));
        assertEquals(Sets.newHashSet(square._1._1, square._1._2, square._2._1, square._2._2, square._3._1, square._3._2,
                square._4._1, square._4._2), face1);

    }

    @Test(expected = IllegalStateException.class)
    public void cantIdentifyEdgeWithItself() {
        NGMap ngMap = NGMap.ngMap(2);
        Square square = addSquare(ngMap);

        ngMap.sew(square._1._1, square._1._2, 2);
    }

    /*
     * On page 7 of Cellular Modeling in Arbitrary Dimension using Generalized Maps, Le ́vy + Mallet, they describe
     * a constraint placed on the n-GMap to avoid identification of two adjacent cells as
     *   ∀0 ≤ i < i + 2 ≤ j ≤ N, the function αi o αj has no fixed point.
     * However, I don't know how to enforce this within the sew operation.
     */
    @Test//(expected = IllegalStateException.class
    public void cantFoldEdge() {
        /* We're going to construct a square in which one age contains 6 darts
         *
         *    *---------------e1 ---------------*
         *   *                                   *
         *   |                                   |
         *  e7                                  e2
         *   |                                   |
         *   *                                   *
         *    *--e6--* *--e5--* *--e4--* *--e3--*
         */

        NGMap ngMap = NGMap.ngMap(2);
        Edge edge1 = addEdge(ngMap);
        Edge edge2 = addEdge(ngMap);
        Edge edge3 = addEdge(ngMap);
        Edge edge4 = addEdge(ngMap);
        Edge edge5 = addEdge(ngMap);
        Edge edge6 = addEdge(ngMap);
        Edge edge7 = addEdge(ngMap);

        ngMap.sew(edge1._1, edge7._2, 1);
        ngMap.sew(edge2._1, edge1._2, 1);
        ngMap.sew(edge3._1, edge2._2, 1);
        ngMap.sew(edge4._1, edge3._2, 1);
        ngMap.sew(edge5._1, edge4._2, 1);
        ngMap.sew(edge6._1, edge5._2, 1);
        ngMap.sew(edge7._1, edge6._2, 1);

        // Should be prohibited
        ngMap.sew(edge4._2, edge5._1, 2);
    }

    /*
     *    d1     d2
     *     *-- --*
     */
    private Edge addEdge(NGMap ngMap) {
        NGMap.Dart dart1 = ngMap.addIsolatedDart();
        NGMap.Dart dart2 = ngMap.addIsolatedDart();

        ngMap.sew(dart1, dart2, 0);

        return new Edge(dart1, dart2);
    }

    private Triangle addTriangle(NGMap ngMap) {
        Edge edge1 = addEdge(ngMap);
        Edge edge2 = addEdge(ngMap);
        Edge edge3 = addEdge(ngMap);

        ngMap.sew(edge1.get_1(), edge3.get_2(), 1);
        ngMap.sew(edge2.get_1(), edge1.get_2(), 1);
        ngMap.sew(edge3.get_1(), edge2.get_2(), 1);

        return new Triangle(edge1, edge2, edge3);
    }

    /*
     *       d1     d2
     *        *--e1--*
     *    d8 *        * d3
     *       |        |
     *      e4       e2
     *       |        |
     *    d7 *        * d4
     *        *--e3--*
     *       d6      d5
     */
    private Square addSquare(NGMap ngMap) {
        Edge edge1 = addEdge(ngMap);
        Edge edge2 = addEdge(ngMap);
        Edge edge3 = addEdge(ngMap);
        Edge edge4 = addEdge(ngMap);

        ngMap.sew(edge1.get_1(), edge4.get_2(), 1);
        ngMap.sew(edge2.get_1(), edge1.get_2(), 1);
        ngMap.sew(edge3.get_1(), edge2.get_2(), 1);
        ngMap.sew(edge4.get_1(), edge3.get_2(), 1);

        return new Square(edge1, edge2, edge3, edge4);
    }

    /*
     *
     *        *--e0--*    *--e0--*
     *       *        *  *        *
     *       |        |  |        |
     *      e3   f0  e1 e3   f2  e1
     *       |        |  |        |
     *       *        *  *        *
     *        *--e2--*    *--e2--*
     *
     */
    private Cube addCube(NGMap ngMap) {
        Square square1 = addSquare(ngMap);
        Square square2 = addSquare(ngMap);
        Square square3 = addSquare(ngMap);
        Square square4 = addSquare(ngMap);
        Square square5 = addSquare(ngMap);
        Square square6 = addSquare(ngMap);

        ngMap.sew(square1.get_1().get_1(), square2.get_3().get_2(), 2);
        ngMap.sew(square1.get_2().get_1(), square3.get_4().get_2(), 2);
        ngMap.sew(square1.get_3().get_1(), square4.get_1().get_2(), 2);
        ngMap.sew(square1.get_4().get_1(), square5.get_2().get_2(), 2);

        ngMap.sew(square2.get_1().get_1(), square6.get_3().get_2(), 2);
        ngMap.sew(square2.get_2().get_1(), square3.get_1().get_2(), 2);
        ngMap.sew(square2.get_4().get_1(), square5.get_1().get_2(), 2);

        ngMap.sew(square3.get_2().get_1(), square6.get_2().get_2(), 2);
        ngMap.sew(square3.get_3().get_1(), square4.get_2().get_2(), 2);

        ngMap.sew(square4.get_3().get_1(), square6.get_1().get_2(), 2);
        ngMap.sew(square4.get_4().get_1(), square5.get_3().get_2(), 2);

        ngMap.sew(square5.get_4().get_1(), square6.get_4().get_2(), 2);

        return new Cube(square1, square2, square3, square4, square5, square6);
    }

    /*
     *
     */
    private Pyramid addPyramid(NGMap ngMap) {
        Square square = addSquare(ngMap);
        Triangle triangle1 = addTriangle(ngMap);
        Triangle triangle2 = addTriangle(ngMap);
        Triangle triangle3 = addTriangle(ngMap);
        Triangle triangle4 = addTriangle(ngMap);

        ngMap.sew(square.get_1().get_1(), triangle1.get_3().get_2(), 2);
        ngMap.sew(square.get_2().get_1(), triangle2.get_1().get_2(), 2);
        ngMap.sew(square.get_3().get_1(), triangle3.get_1().get_2(), 2);
        ngMap.sew(square.get_4().get_1(), triangle4.get_2().get_2(), 2);

        ngMap.sew(triangle1.get_1().get_1(), triangle4.get_1().get_2(), 2);
        ngMap.sew(triangle1.get_2().get_1(), triangle2.get_2().get_2(), 2);

        ngMap.sew(triangle2.get_3().get_1(), triangle3.get_2().get_2(), 2);

        ngMap.sew(triangle3.get_3().get_1(), triangle4.get_3().get_2(), 2);

        return new Pyramid(square, triangle1, triangle2, triangle3, triangle4);
    }

    public House addHouse(NGMap ngMap) {
        Cube cube = addCube(ngMap);
        Pyramid pyramid = addPyramid(ngMap);

        // Only need to sew dart to sew the volumes together!
        ngMap.sew(cube._1._1._1, pyramid.square._1._1, 3);

        return new House(cube, pyramid);
    }

    @Data
    private static final class Edge {
        private final NGMap.Dart _1;
        private final NGMap.Dart _2;
    }

    @Data
    private static final class Triangle {
        private final Edge _1;
        private final Edge _2;
        private final Edge _3;
    }

    @Data
    private static final class Square {
        private final Edge _1;
        private final Edge _2;
        private final Edge _3;
        private final Edge _4;
    }

    @Data
    private static final class Pyramid {
        private final Square square;
        private final Triangle triangle1;
        private final Triangle triangle2;
        private final Triangle triangle3;
        private final Triangle triangle4;
    }

    @Data
    private static final class Cube {
        private final Square _1;
        private final Square _2;
        private final Square _3;
        private final Square _4;
        private final Square _5;
        private final Square _6;
    }

    @Data
    private static final class House {
        private final Cube cube;
        private final Pyramid pyramid;
    }


}