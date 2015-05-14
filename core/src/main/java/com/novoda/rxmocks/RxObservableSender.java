package com.novoda.rxmocks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rx.Observable;

public class RxObservableSender<T> {

    private final Object repo;
    private final Observable<T> source;

    RxObservableSender(Object repo, Observable<T> source) {
        this.repo = repo;
        this.source = source;
    }

    /**
     * Send the events from {@code source} to the given mocked {@code observable}
     * @param observable The mocked observable to inject events into.
     */
    public void to(Observable<T> observable) {
        try {
            Method send = repo.getClass().getMethod(RxMockerInvocationProxy.METHOD_NAME_USE_EVENTS_FROM, Observable.class, Observable.class);
            send.invoke(repo, source, observable);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException("with not called on a mocked object please use RxMocks.mock(Class) to generate the repo mock", e);
        }
    }
}
