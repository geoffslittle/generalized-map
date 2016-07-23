package com.geoffslittle.datastructure.generalizedmap;

/*
 * Intuitive Cell Types
 *  0-cells are vertices
 *  1-cells are edges
 *  2-cells are facets
 *  3-cells are volumes

 * Relations
 *  Boundary: for each i-cell there is a list of (i-1)-cells in it's boundary.
 *  Incident: two cells are incident if there's a path of cells decreasing in dimension from the cell with the largest
 *   dimension to the cell with the smallest, such that each cell in the path belongs to the boundary of the previous
 *  Adjacent: two cells are adjacent if there's an (i-1)-cell incident to each

 * Cells contain darts and darts belong to cells
 *
 */
public class TwoDGMap<E> {

    // Points to neighbor vertices
    private final Involution<E> b0;
    // Points to neighbor edges
    private final Involution<E> b1;
    // Points to neighbor faces
    private final Involution<E> b2;

//    // Points to darts that represent a vertex
//    private final Function<E, E> phi0;
//    // Points to darts that represent an edge
//    private final Function<E, E> phi1;
//    // Points to darts that represent a face
//    private final Function<E, E> phi2;

    private TwoDGMap(Involution<E> b0, Involution<E> b1, Involution<E> b2) {
        this.b0 = b0;
        this.b1 = b1;
        this.b2 = b2;
//        this.phi0 = e -> b2.get(b1.get(e));
//        this.phi1 = e -> b2.get(b0.get(e));
//        this.phi2 = e -> b1.get(b0.get(e));
    }

    public static <E> TwoDGMap<E> new2DGMap() {
        return new TwoDGMap<E>(Involution.newInvolution(), Involution.newInvolution(), Involution.newInvolution());
    }

//    public void sewVertices(E dart, E codart) {
//        b0.addMapping(dart, codart);
//    }
//
//    public void sewEdges(E dart, E codart) {
//        b1.addMapping(dart, codart);
//    }
//
//    public void sewFaces(E dart, E codart) {
//        b2.addMapping(dart, codart);
//    }
//
//    public Set<E> getVertexOrbit(E dart) {
//        E current = null;
//        for (E start = dart; current != start ; current = phi0.apply(current)) {
//
//        }
//
//        return null;
//    }

}
