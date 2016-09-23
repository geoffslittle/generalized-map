package com.geoffslittle.datastructure.generalizedmap;

import com.geoffslittle.datastructure.maps.Attribute;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import fj.P;
import fj.P2;
import lombok.NonNull;
import lombok.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This implementation of an n-Gmap guarantees that any interaction with an n-GMap produces a valid n-GMap
 */
public class NGMap {

    /**
     * The list of alphas that define the mappings of the n-Gmap. We assume each alpha to be a partial involution
     * without fixed point.
     */
    @NonNull
    private final List<Involution<Dart>> alphas;
    @NonNull
    private final SafeMap<P2<Dart, Integer>, Attribute> attributes;
    // TODO: Decide if this should be generic in A or accept any Object (different attributes for different i-cells)


    private NGMap() {
        this.alphas = Lists.newArrayList(Involution.involution());
        this.attributes = SafeMap.newSafeMap();
    }

    /**
     * Basic public static constructor
     * @return a 0-GMap
     */
    public static NGMap ngMap() {
        return new NGMap();
    }

    public int dimension() {
        return alphas.size() - 1;
    }

    public boolean increaseDimension() {
        return alphas.add(Involution.involution());
    }

    public void decreaseDimension() {
        Preconditions.checkState(0 < dimension(), "Can't decrease dimension below 0");
        Involution<Dart> toRemove = alphas.get(dimension());
        // Decreasing a dimension is prohibited if the alpha that represents associations in the dimension to remove
        // contains any non-empty value mapping
        Preconditions.checkState(toRemove.domainSet().stream()
                .allMatch(element -> !toRemove.get(element).isPresent()));
        alphas.remove(dimension());
    }

    /**
     * Convenience constructor for constructing an n-GMap given a dimension, n
     * @param n, the desired dimension of the n-GMap
     * @return an n-GMap of desired dimension, n
     */
    public static NGMap ngMap(int n) {
        NGMap nGMap = NGMap.ngMap();
        IntStream.range(0, n).forEach(i -> nGMap.increaseDimension());
        return nGMap;
    }

    private void checkValidDimension(int i) {
        // i must be a valid value
        Preconditions.checkState(0 <=i && i <= dimension(), "0 <= i <= n");
    }

    /**
     * Checks if a given dart is free in the given alpha.  If the dart is not "in" the alpha, it's considered i-free.
     * @param alpha
     * @param dart
     * @return true iff the dart is free in the given alpha, false otherwise
     */
    private static boolean isFree(Involution<Dart> alpha, Dart dart) {
        return !alpha.containsElement(dart) || !alpha.get(dart).isPresent();
    }

    public Boolean isIFree(@NonNull Dart dart, int i) {
        checkValidDimension(i);
        return isFree(alphas.get(i), dart);
    }

    public boolean containsDart(@NonNull Dart dart) {
        return alphas.stream().map(alpha -> alpha.containsElement(dart)).reduce(false, (p, q) -> p || q);
    }

    public boolean isIsolated(@NonNull Dart dart) {
        return alphas.stream().map(alpha -> isFree(alpha, dart)).reduce(true, (p, q) -> p && q);
    }

    public Dart addIsolatedDart() {
        Dart dart = new Dart(ids.next());
        alphas.stream().forEach(alpha -> alpha.put(dart));
        return dart;
    }

    public void removeIsolatedDart(@NonNull Dart dart) {
        Preconditions.checkState(isIsolated(dart), "dart is not isolated");
        alphas.stream().forEach(alpha -> alpha.remove(dart));
    }

    public Set<Dart> darts() {
        return alphas.stream()
                .map(Involution::domainSet)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private List<Involution<Dart>> intsToAlphas(List<Integer> ints) {
        return ints.stream().map(i -> {checkValidDimension(i); return alphas.get(i);}).collect(Collectors.toList());
    }

    public Iterator<Dart> genericIterator(@NonNull Dart dart, @NonNull List<Integer> ints) {
        Function<Dart, List<Dart>> f = d -> intsToAlphas(ints).stream()
                .map(alpha -> alpha.get(d))
                // TODO: When Java 9 is released, the filtering and mapping of Optionals should be easier
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return BreadthFirstSearch.breadthFirstSearch(dart, f);
    }

    private List<Integer> filterIntList(@NonNull Integer i, Predicate<Integer> predicate) {
        return IntStream.range(0, dimension() + 1)
                .filter(j -> predicate.test(j))
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * We use this range in order to grab orbits related to i-sewing
     * @param i
     * @return
     */
    private List<Integer> specialRange(@NonNull Integer i) {
        return filterIntList(i, j -> j <= i-2 || i+2 <= j);
    }

    private List<Integer> excludeFromRange(@NonNull Integer i) {
        return filterIntList(i, j -> j != i);
    }

    private boolean hasIsomorphismAndNonEqualOrbits(Iterator<Dart> leftIt, Iterator<Dart> rightIt,
            List<Involution<Dart>> alphas) {
        Set<Dart> leftOrbit = new HashSet<>();
        Set<Dart> rightOrbit = new HashSet<>();
        Map<Dart, Dart> iso = new HashMap<>();
        while (leftIt.hasNext() && rightIt.hasNext()) {
            Dart leftCurr = leftIt.next();
            Dart rightCurr = rightIt.next();
            leftOrbit.add(leftCurr);
            rightOrbit.add(rightCurr);
            iso.put(leftCurr, rightCurr);
            for (Involution<Dart> alpha : alphas) {
                if (alpha.get(leftCurr).isPresent()
                        && iso.containsKey(alpha.get(leftCurr).get())
                        && alpha.get(rightCurr).isPresent()
                        && !iso.get(alpha.get(leftCurr).get()).equals(alpha.get(rightCurr).get())) {
                    return false;
                }
            }
        }

        return !(leftIt.hasNext() || rightIt.hasNext() || Objects.equals(leftOrbit, rightOrbit));
    }

    private boolean dartsNotEqualAndFree(Dart leftDart, Dart rightDart, int i) {
        return !(leftDart.equals(rightDart) || !isIFree(leftDart, i) || !isIFree(rightDart, i));
    }

    /**
     * Two darts d and d' are i-sewable iff there exists an isomorphism between orbits o and o'
     * @param leftDart
     * @param rightDart
     * @param i
     * @return
     */
    public boolean isSewable(@NonNull Dart leftDart, @NonNull Dart rightDart, int i) {
        checkValidDimension(i);
        if (dimension() <= 1 || (dimension() == 2 && i == 1)) {
            // For 0- and 1-GMaps, a pair of darts are sewable iff they are free, similarly for 2-GMaps where i=1
            return dartsNotEqualAndFree(leftDart, rightDart, i);
        }
        List<Integer> ints = specialRange(i);
        List<Involution<Dart>> alphas = intsToAlphas(ints);

        return dartsNotEqualAndFree(leftDart, rightDart, i) &&
                hasIsomorphismAndNonEqualOrbits(genericIterator(leftDart, ints),
                        genericIterator(rightDart, ints), alphas);
    }

    public void sew(@NonNull Dart leftDart, @NonNull Dart rightDart, int i) {
        checkValidDimension(i);
        Preconditions.checkState(isSewable(leftDart, rightDart, i), "Darts are not sewable");

        Involution<Dart> alpha = alphas.get(i);
        if (dimension() <= 1 || (dimension() == 2 && i == 1)) {
            // For 0- and 1-GMaps, a pair of darts are sewable iff they are free, similarly for 2-GMaps where i=1
            alpha.put(leftDart, rightDart);
            return;
        }

        Iterator<Dart> leftIt = genericIterator(leftDart, specialRange(i));
        Iterator<Dart> rightIt = genericIterator(rightDart, specialRange(i));
        while (leftIt.hasNext()) {
            Dart leftDartP = leftIt.next();
            Dart rightDartP = rightIt.next();
            alpha.put(leftDartP, rightDartP);
        }
    }

    public void unsew(@NonNull Dart dart, int i) {
        checkValidDimension(i);

        // It's fine if it's already i-free, the client is happy
        // Preconditions.checkState(!isIFree(dart, i), "dart is already i-free");

        Involution<Dart> alpha = alphas.get(i);
        List<Dart> orbit = Lists.newArrayList(genericIterator(dart, specialRange(i)));
        for (Dart curr : orbit) {
            alpha.remove(curr);
        }
    }

    public Edge addEdge() {
        NGMap.Dart dart1 = addIsolatedDart();
        NGMap.Dart dart2 = addIsolatedDart();

        sew(dart1, dart2, 0);

        return new Edge(dart1, dart2);
    }

    public List<Edge> addNPolygon(@NonNull Integer n) {
        Edge firstEdge = addEdge();
        Edge lastEdge = firstEdge;
        for (int i = 1; i < n; i++) {
            Edge currEdge = addEdge();
            sew(lastEdge.get_2(), currEdge.get_1(), 1);
            lastEdge = currEdge;
        }
        sew(lastEdge.get_2(), firstEdge.get_1(), 1);
        return null;
    }

    public List<Dart> iCell(@NonNull Dart dart, @NonNull Integer i) {
        return Lists.newArrayList(genericIterator(dart, excludeFromRange(i)));
    }

    public Attribute putAttribute(@NonNull Dart dart, @NonNull Integer i, Attribute attribute) {
        // Get the i-cell for the given dart
        List<Dart> iCell = iCell(dart, i);
        iCell.stream().forEach(curr -> Preconditions.checkState(Optional.empty().equals(attributes.get(P.p(curr, i)))));
        attributes.put(P.p(dart, i), Optional.of(attribute));
        return attribute;
    }

    public void removeAttribute(@NonNull Dart dart, @NonNull Integer i) {
        // Get the i-cell for the given dart
        List<Dart> iCell = iCell(dart, i);
        // Remove any and all associations with each dart in the i-cell
        iCell.stream().forEach(curr -> attributes.put(P.p(curr, i), Optional.empty()));
    }

    public Optional<Attribute> getAttribute(@NonNull Dart dart, @NonNull Integer i) {
        List<Dart> iCell = iCell(dart, i);
        return iCell.stream()
                .map(curr -> attributes.get(P.p(curr, i)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private final Iterator<Integer> ids = new Iterator<Integer>() {
        private int i = 1;
        @Override
        public boolean hasNext() {
            return true;
        }
        @Override
        public Integer next() {
            return i++;
        }
    };

    @Value
    public static final class Dart {
        private final int id;
    }

    @Value
    public static final class Edge {
        private final Dart _1;
        private final Dart _2;
    }

}
