package com.geoffslittle.datastructure.maps;

import com.geoffslittle.datastructure.generalizedmap.NGMap;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

public class TwoGMap {

    private static final Integer VERTEX_DIM = 0;
    private static final Integer EDGE_DIM = 1;

    private final NGMap ngMap;

    public NGMap ngMap() {
        return ngMap;
    }

    private TwoGMap(NGMap ngMap) {
        this.ngMap = ngMap;
    }

    public static TwoGMap twoGMap() {
        return new TwoGMap(NGMap.ngMap(2));
    }

    private void checkNonEmptyDarts(@NonNull List<NGMap.Dart> darts) {
        Preconditions.checkState(!darts.isEmpty());
    }

    public Vertex addVertex() {
        NGMap.Dart dart = ngMap.addIsolatedDart();
        return new Vertex(ngMap.iCell(dart, VERTEX_DIM));
    }

    public void removeVertex(@NonNull Vertex vertex) {
        checkNonEmptyDarts(vertex.getDarts());

        vertex.getDarts().stream()
                .forEach(dart -> ngMap.unsew(dart, VERTEX_DIM));
    }

    public void addVertexAttribute(@NonNull Vertex vertex, @NonNull Attribute attribute) {
        checkNonEmptyDarts(vertex.getDarts());

        ngMap.putAttribute(vertex.getDarts().get(0), VERTEX_DIM, attribute);
    }

    public Edge addEdge(@NonNull Vertex left, @NonNull Vertex right) {
        checkNonEmptyDarts(left.getDarts());
        checkNonEmptyDarts(right.getDarts());

        NGMap.Dart firstLeftDart = left.getDarts().get(0);
        NGMap.Dart firstRightDart = right.getDarts().get(0);
        if (ngMap.isSewable(firstLeftDart, firstRightDart, EDGE_DIM)) {
            ngMap.sew(firstLeftDart, firstRightDart, EDGE_DIM);
            return new Edge(ngMap.iCell(firstLeftDart, EDGE_DIM));
        }
        NGMap.Dart newLeftDart = ngMap.addIsolatedDart();
        ngMap.sew(firstLeftDart, newLeftDart, VERTEX_DIM);
        if (ngMap.isSewable(newLeftDart, firstRightDart, EDGE_DIM)) {
            ngMap.sew(newLeftDart, firstRightDart, EDGE_DIM);
            return new Edge(ngMap.iCell(newLeftDart, EDGE_DIM));
        }
        NGMap.Dart newRightDart = ngMap.addIsolatedDart();
        ngMap.sew(firstRightDart, newRightDart, VERTEX_DIM);
        if (ngMap.isSewable(newLeftDart, newRightDart, EDGE_DIM)) {
            ngMap.sew(newLeftDart, newRightDart, EDGE_DIM);
            return new Edge(ngMap.iCell(newLeftDart, EDGE_DIM));
        }
        throw new IllegalStateException("bad");
    }

    public void removeEdge(@NonNull Edge edge) {
        checkNonEmptyDarts(edge.getDarts());

        edge.getDarts().stream()
                .forEach(dart -> ngMap.unsew(dart, EDGE_DIM));
    }

    public void addEdgeAttribute(@NonNull Edge edge, @NonNull Attribute attribute) {
        checkNonEmptyDarts(edge.getDarts());

        ngMap.putAttribute(edge.getDarts().get(0), EDGE_DIM, attribute);
    }

    @Value
    public static final class Vertex {
        @NonNull
        private final List<NGMap.Dart> darts;
    }

    @Value
    public static final class Edge {
        @NonNull
        private final List<NGMap.Dart> darts;
    }

}
