package com.novoda.rxmocks;

import rx.Notification;
import rx.Observable;

public interface RxMockerInvocationProxy {

    String METHOD_NAME_USE_EVENTS_FROM = "useEventsFrom";
    String METHOD_NAME_GET_EVENTS_FOR = "getEventsFor";
    String METHOD_NAME_PROVIDES = "provides";
    String METHOD_NAME_RESET_MOCKS = "resetMocks";

    <T> void useEventsFrom(Observable<T> source, Observable<T> observable);

    <T> Observable<Notification<T>> getEventsFor(Observable<T> observable);

    <T> boolean provides(Observable<T> observable);

    void resetMocks();

}
