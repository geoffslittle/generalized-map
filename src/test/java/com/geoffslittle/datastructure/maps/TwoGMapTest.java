package com.geoffslittle.datastructure.maps;

import lombok.Value;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TwoGMapTest {

    private static final String VERTEX_1 = "vertex_1";
    private static final String VERTEX_2 = "vertex_2";
    private static final String VERTEX_3 = "vertex_3";
    private static final String VERTEX_4 = "vertex_4";
    private static final String EDGE_1 = "edge-1";
    private static final String EDGE_2 = "edge-2";
    private static final String EDGE_3 = "edge-3";
    private static final String EDGE_4 = "edge-4";

    @Test
    public void addVertexAddsDart() {
        TwoGMap twoGMap = TwoGMap.twoGMap();

        TwoGMap.Vertex vertex = twoGMap.addVertex();
        assertEquals(vertex.getDarts(), twoGMap.ngMap().iCell(vertex.getDarts().get(0), 0));
    }

    @Test
    public void addEdgeAddsDarts() {
        TwoGMap twoGMap = TwoGMap.twoGMap();

        TwoGMap.Vertex vertex1 = twoGMap.addVertex();
        TwoGMap.Vertex vertex2 = twoGMap.addVertex();

        assertEquals(vertex.getDarts(), twoGMap.ngMap().iCell(vertex.getDarts().get(0), 0));
    }

    @Test
    public void canCreateSquare() {
        TwoGMap twoGMap = TwoGMap.twoGMap();

        TwoGMap.Vertex vertex1 = twoGMap.addVertex();
        TwoGMap.Vertex vertex2 = twoGMap.addVertex();
        TwoGMap.Vertex vertex3 = twoGMap.addVertex();
        TwoGMap.Vertex vertex4 = twoGMap.addVertex();

        TwoGMap.Edge edge1 = twoGMap.addEdge(vertex1, vertex2);
        TwoGMap.Edge edge2 = twoGMap.addEdge(vertex2, vertex3);
        TwoGMap.Edge edge3 = twoGMap.addEdge(vertex3, vertex4);
        TwoGMap.Edge edge4 = twoGMap.addEdge(vertex4, vertex1);

        twoGMap.addVertexAttribute(vertex1, new StringAttr(VERTEX_1));
        twoGMap.addVertexAttribute(vertex2, new StringAttr(VERTEX_2));
        twoGMap.addVertexAttribute(vertex3, new StringAttr(VERTEX_3));
        twoGMap.addVertexAttribute(vertex4, new StringAttr(VERTEX_4));

        twoGMap.addEdgeAttribute(edge1, new StringAttr(EDGE_1));
        twoGMap.addEdgeAttribute(edge2, new StringAttr(EDGE_2));
        twoGMap.addEdgeAttribute(edge3, new StringAttr(EDGE_3));
        twoGMap.addEdgeAttribute(edge4, new StringAttr(EDGE_4));
    }

    @Value
    private static final class StringAttr implements Attribute {
        private final String string;
    }

}