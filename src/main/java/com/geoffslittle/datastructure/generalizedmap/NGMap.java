package com.geoffslittle.datastructure.generalizedmap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * This implementation of an n-Gmap guarantees that any interaction with an n-GMap produces a valid n-GMap
 */
public class NGMap {


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

    /**
     * The list of alphas that define the mappings of the n-Gmap. We assume each alpha to be a partial involution
     * without fixed point.
     */
    @NonNull
    public final List<Involution<Dart>> alphas;

    private NGMap() {
        this.alphas = newArrayList(Involution.involution());
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

    private List<Involution<Dart>> intsToAlphas(List<Integer> ints) {
        return ints.stream().map(i -> {checkValidDimension(i); return alphas.get(i);}).collect(Collectors.toList());
    }

    public Iterator<Dart> genericIterator(@NonNull Dart dart, @NonNull List<Integer> ints) {
        return GenericDartIterator.genericDartIterator(dart, intsToAlphas(ints));
    }

    /**
     * We use this range in order to grab orbits related to i-sewing
     * @param i
     * @return
     */
    private List<Integer> specialRange(int i) {
        return IntStream.range(0, dimension() + 1)
                .filter(j -> j <= i-2 || i+2 <= j)
                .boxed()
                .collect(Collectors.toList());
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
                if (iso.containsKey(alpha.get(leftCurr)) &&
                        !iso.get(alpha.get(leftCurr)).equals(alpha.get(rightCurr))) {
                    return false;
                }
            }
        }

        if (leftIt.hasNext() || rightIt.hasNext() || Objects.equals(leftOrbit, rightOrbit)) {
            return false;
        }
        return true;
    }

    private boolean dartsNotEqualAndFree(Dart leftDart, Dart rightDart, int i) {
        if (leftDart.equals(rightDart) || !isIFree(leftDart, i) || !isIFree(rightDart, i)) {
            return false;
        }
        return true;
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
        Preconditions.checkState(!isIFree(dart, i), "dart is already i-free");

        Involution<Dart> alpha = alphas.get(i);
        List<Dart> orbit = Lists.newArrayList(genericIterator(dart, specialRange(i)));
        for (Dart curr : orbit) {
            alpha.remove(curr);
        }
    }

    @Value
    public final class Dart {
        private final int id;
    }

    public static class GenericDartIterator implements Iterator<Dart> {

        private final Queue<Dart> queue;
        private final Set<Dart> seen;
        private final List<Involution<Dart>> alphas;

        private GenericDartIterator(Queue<Dart> queue, Set<Dart> seen, List<Involution<Dart>> alphas) {
            this.queue = queue;
            this.seen = seen;
            this.alphas = alphas;
        }

        public static final Iterator<Dart> genericDartIterator(@NonNull Dart sourceDart,
                @NonNull List<Involution<Dart>> alphas) {
            Queue<Dart> queue = new LinkedList<>();
            queue.add(sourceDart);
            return new GenericDartIterator(queue, new HashSet<>(), alphas);
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public Dart next() {
            if (!hasNext()) {
                // Can't continue if there is no "next"
                throw new NoSuchElementException();
            }
            // Grab a dart from the top of the queue
            Dart current = queue.poll();
            // Check if we've already processed this dart
            if (!seen.contains(current)) {
                // Get the orbit of the current dart as defined by the given list of alphas
                for (Involution<Dart> alpha : alphas) {
                    Optional<Dart> next = alpha.get(current);
                    // Not sure if I should need to check if the queue doesn't contain it the current's neighbor
                    // If not, though, there could be a unseen dart which is the neighbor of two darts that are visited,
                    // effectively adding the unseen dart to the queue twice.  This is bad because we always draw and
                    // return the top of the queue.
                    if (next.isPresent() && !seen.contains(next.get()) && !queue.contains(next.get())) {
                        queue.offer(next.get());
                    }
                }
                seen.add(current);
            }
            return current;
        }
    }

}
