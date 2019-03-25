package io.reactivex;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class Observable<T> implements ObservableSource<T> {

    public final Observable<T> filter(Predicate<? super T> predicate) {
        throw new RuntimeException("Stub!");
    }

    public final <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        throw new RuntimeException("Stub!");
    }

    public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        throw new RuntimeException("Stub!");
    }

    public final <R> Observable<R> scan(final R initialValue, BiFunction<R, ? super T, R> accumulator) {
        throw new RuntimeException("Stub!");
    }

    @Override public void subscribe(final Observer<? super T> observer) {
        throw new RuntimeException("Stub!");
    }
}
