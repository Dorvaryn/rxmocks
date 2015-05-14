package com.novoda.rxmocks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rx.Notification;
import rx.Observable;

public class RxMocker {

    private final Object repo;

    RxMocker(Object repo) {
        this.repo = repo;
    }

    /**
     * Inject the events from given {@code source} into a mocked pipeline
     * @param source The observable producing the events to inject
     * @param <T> The type of this observable
     * @return A sender object to define into which pipeline to inject the events.
     */
    public <T> RxObservableSender<T> sendEventsFrom(Observable<T> source) {
        return new RxObservableSender<>(repo, source);
    }

    /**
     * Get an observable representing the events going through a given {@code observable} mocked pipeline.
     * Subscribing to this observable does not affect the original {@code observable}
     * @param observable The mocked pipeline to observe
     * @param <T> The type of this observable
     * @return An observable representiong the events passing through {@code observable}
     */
    public <T> Observable<Notification<T>> getEventsFor(Observable<T> observable) {
        try {
            Method send = repo.getClass().getMethod(RxMockerInvocationProxy.METHOD_NAME_GET_EVENTS_FOR, Observable.class);
            return (Observable<Notification<T>>) send.invoke(repo, observable);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException("with not called on a mocked object please use RxMocks.mock(Class) to generate the repo mock", e);
        }
    }

    /**
     * Resets all the mocked observables in this repository
     */
    public void resetMocks() {
        try {
            Method send = repo.getClass().getMethod(RxMockerInvocationProxy.METHOD_NAME_RESET_MOCKS);
            send.invoke(repo);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException("with not called on a mocked object please use RxMocks.mock(Class) to generate the repo mock", e);
        }
    }

    /**
     * Checks if this repository is providing a given mocked {@code observable}
     * @param observable The mocked observable we want to know if the repository provides
     * @param <T> The type of this observable
     * @return true if this repository provides the given {@code observable} false otherwise.
     */
    public <T> boolean provides(final Observable<T> observable) {
        try {
            Method send = repo.getClass().getMethod(RxMockerInvocationProxy.METHOD_NAME_PROVIDES, Observable.class);
            return (Boolean) send.invoke(repo, observable);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException("with not called on a mocked object please use RxMocks.mock(Class) to generate the repo mock", e);
        }
    }
}
