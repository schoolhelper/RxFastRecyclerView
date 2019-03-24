package io.reactivex;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class Observable<T> {

    public final Observable<T> filter(Predicate<? super T> predicate) {
        throw new RuntimeException("Stub!");
    }

    public final <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        throw new RuntimeException("Stub!");
    }

    public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        throw new RuntimeException("Stub!");
    }
}
