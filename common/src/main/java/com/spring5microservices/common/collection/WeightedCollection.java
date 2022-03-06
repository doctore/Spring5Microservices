package com.spring5microservices.common.collection;

import java.security.SecureRandom;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import static java.util.Optional.empty;

/**
 * Collection used to work with a weighted list of elements, every one related with an specific weight value.
 *
 * @param <T>
 *     Elements stored in the collection
 */
public class WeightedCollection<T> {

    private SecureRandom randomElementSelector;
    private final NavigableMap<Integer, T> weightedMap;
    private int totalWeight;


    private WeightedCollection() {
        this.weightedMap = new TreeMap<>();
        this.totalWeight = 0;
    }

    private WeightedCollection(SecureRandom randomElementSelector) {
        this();
        this.randomElementSelector = randomElementSelector;
    }

    public static <T> WeightedCollection<T> of(SecureRandom randomElementSelector) {
        return new WeightedCollection<>(randomElementSelector);
    }


    /**
     * Include a new weighted element in the collection.
     *
     * @param weight
     *    Weight value related with {@code toInsert}
     * @param toInsert
     *    New element to insert
     */
    public void add(int weight, T toInsert) {
        if (0 >= weight) {
            throw new IllegalArgumentException("weight should be a positive value");
        }
        if (null == toInsert) {
            throw new IllegalArgumentException("toInsert must not be null");
        }
        totalWeight += weight;
        weightedMap.put(totalWeight, toInsert);
    }


    /**
     * Include a new weighted element in the collection (a new {@link WeightedCollection} will be returned).
     *
     * @param weight
     *    Weight value related with {@code toInsert}
     * @param toInsert
     *    New element to insert
     *
     * @return new updated {@link WeightedCollection}
     */
    public WeightedCollection<T> addAndThen(int weight, T toInsert) {
        WeightedCollection<T> result = clone();
        result.add(weight, toInsert);
        return result;
    }


    /**
     * Compares the given {@link WeightedCollection} to know if is equals to the current one.
     *
     * @param other
     *    {@link WeightedCollection} to compare
     *
     * @return {@code true} if both collections are equals, {@code false} otherwise.
     */
    public boolean equals(WeightedCollection<?> other) {
        if (other == this)
            return true;

        if (null == other || other.size() != size())
            return false;

        return weightedMap.equals(other.weightedMap);
    }


    /**
     * Return {@code true} if the collection has no elements, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return 0 == size();
    }


    /**
     *    Return a weighted {@link Set} of the elements included in the {@link WeightedCollection}. Every new element inserted in the
     * returned {@link Set} will be weighted against the elements of the {@link WeightedCollection} not included on it.
     *
     * @return weighted {@link Set} of the elements in the collection
     */
    public Set<T> toSet() {
        Set<T> result = new LinkedHashSet<>();
        WeightedCollection<T> copy = clone();

        for (int i = 0; i < size(); i++) {
            Optional<T> nextValue = copy.next();
            if (nextValue.isPresent()) {
                copy = copy.removeAndThen(nextValue.get());
                result.add(nextValue.get());
            }
        }
        return result;
    }


    /**
     *    Return one of the stored elements taking into account their related "weight values". It is important to take into account that
     * in every invocation, all stored elements will be used to return a new one (in a randomly way), so it is possible getting same
     * elements more than once.
     *
     * @return {@link Optional} of {@code E}
     */
    public Optional<T> next() {
        if (weightedMap.isEmpty()) {
            return empty();
        }
        int value = randomElementSelector.nextInt(totalWeight);
        return Optional.of(weightedMap.higherEntry(value).getValue());
    }


    /**
     * Remove the given element from the current collection (a new {@link WeightedCollection} will be returned).
     *
     * @param toRemove
     *    Element to remove
     *
     * @return new updated {@link WeightedCollection}
     */
    public WeightedCollection<T> removeAndThen(T toRemove) {
        WeightedCollection<T> result = of(randomElementSelector);
        int totalAccumulated = 0;

        for (Map.Entry<Integer, T> entry : weightedMap.entrySet()) {
            if (!entry.getValue().equals(toRemove)) {
                result.add(entry.getKey() - totalAccumulated, entry.getValue());
            }
            totalAccumulated = entry.getKey();
        }
        return result;
    }


    /**
     * Return the number of stored elements in {@link WeightedCollection}
     */
    public int size() {
        return weightedMap.size();
    }


    protected WeightedCollection<T> clone() {
        WeightedCollection<T> result = of(randomElementSelector);
        int totalAccumulated = 0;
        for (Map.Entry<Integer, T> entry : weightedMap.entrySet()) {
            result.add(entry.getKey() - totalAccumulated, entry.getValue());
            totalAccumulated = entry.getKey();
        }
        return result;
    }

}
