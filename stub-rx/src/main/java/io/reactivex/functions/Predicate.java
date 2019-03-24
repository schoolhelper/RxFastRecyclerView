package io.reactivex.functions;

public interface Predicate<T> {
    /**
     * Test the given input value and return a boolean.
     * @param t the value
     * @return the boolean result
     * @throws Exception on error
     */
    boolean test(T t) throws Exception;
}