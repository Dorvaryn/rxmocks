package com.novoda.rxmocks;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subjects.ClearableBehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.BooleanSubscription;

import static com.novoda.rxmocks.Functions.infinite;

class RxMocksInvocationHandler implements InvocationHandler {

    private final Map<String, Observable> observableHashMap = new HashMap<>();
    private Map<Observable, Pair<ClearableBehaviorSubject<Notification>, PublishSubject<Notification>>> mapSubject = new HashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isUseEventsFrom(method)) {
            Observable arg = (Observable) args[0];
            arg.materialize().lift(infinite()).subscribe(mapSubject.get(args[1]).first);
            return null;
        }
        if (isGetEventsFor(method)) {
            Pair<ClearableBehaviorSubject<Notification>, PublishSubject<Notification>> subjectPair = mapSubject.get(args[0]);
            if (subjectPair == null) {
                throw new IllegalArgumentException("The observable " + args[0] +
                        " is not provided by this repo use the provides(Observable o) method to check first");
            }
            return Observable.zip(subjectPair.first, subjectPair.second, unzip())
                    .lift(clearOnUnsubscribe(args[0]));
        }
        if (isProvides(method)) {
            return observableHashMap.containsValue(args[0]);
        }
        if (isResetMocks(method)) {
            observableHashMap.clear();
            mapSubject.clear();
            return null;
        }
        if (method.getReturnType().equals(Observable.class)) {
            if (!observableHashMap.containsKey(getKeyFor(method, args))) {
                initialiseMockedObservable(method, args);
            }
            return observableHashMap.get(getKeyFor(method, args));
        }
        throw new IllegalArgumentException("Method " + method + " not handled");
    }

    private AddUnsubscribe clearOnUnsubscribe(final Object arg) {
        return new AddUnsubscribe(
                BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        mapSubject.get(arg).first.clear();
                    }
                })
        );
    }

    private void initialiseMockedObservable(Method method, Object[] args) {
        ClearableBehaviorSubject<Notification> subject = ClearableBehaviorSubject.create();
        PublishSubject<Notification> notificationSubject = PublishSubject.create();
        final String keyForArgs = getKeyFor(method, args);
        final Observable observable = subject
                .dematerialize()
                .doOnEach(new NotifyDataEvent(notificationSubject))
                .lift(new SwallowUnsubscribe());
        observableHashMap.put(keyForArgs, observable);
        mapSubject.put(observable, new Pair<>(subject, notificationSubject));
    }

    private String getKeyFor(Method method, Object[] args) {
        StringBuilder keyBuilder = new StringBuilder(method.getName());
        int index = 0;
        for (Class<?> type : method.getParameterTypes()) {
            keyBuilder.append('#').append(type.getSimpleName()).append('-').append(args[index++].hashCode());
        }
        return keyBuilder.toString();
    }

    private static boolean isProvides(Method method) {
        return method.getName().equals(RxMockerInvocationProxy.METHOD_NAME_PROVIDES)
                && method.getDeclaringClass().equals(RxMockerInvocationProxy.class);
    }

    private static boolean isGetEventsFor(Method method) {
        return method.getName().equals(RxMockerInvocationProxy.METHOD_NAME_GET_EVENTS_FOR)
                && method.getDeclaringClass().equals(RxMockerInvocationProxy.class);
    }

    private static boolean isResetMocks(Method method) {
        return method.getName().equals(RxMockerInvocationProxy.METHOD_NAME_RESET_MOCKS)
                && method.getDeclaringClass().equals(RxMockerInvocationProxy.class);
    }

    private static boolean isUseEventsFrom(Method method) {
        return method.getName().equals(RxMockerInvocationProxy.METHOD_NAME_USE_EVENTS_FROM)
                && method.getDeclaringClass().equals(RxMockerInvocationProxy.class)
                && method.getParameterTypes()[0].equals(Observable.class);
    }

    private static class NotifyDataEvent<T> implements Action1<Notification<? super T>> {

        private final PublishSubject<Notification<T>> publishSubject;

        public NotifyDataEvent(PublishSubject<Notification<T>> publishSubject) {
            this.publishSubject = publishSubject;
        }

        @Override
        public void call(Notification<? super T> notification) {
            publishSubject.onNext((Notification<T>) notification);
        }
    }

    private static Func2<Notification, Notification, Notification> unzip() {
        return new Func2<Notification, Notification, Notification>() {
            @Override
            public Notification call(Notification first, Notification second) {
                return second;
            }
        };
    }

    private static class AddUnsubscribe<T> implements Observable.Operator<T, T> {

        private final Subscription unsubscribe;

        private AddUnsubscribe(Subscription unsubscribe) {
            this.unsubscribe = unsubscribe;
        }

        @Override
        public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
            subscriber.add(unsubscribe);
            return subscriber;
        }

    }

    private static class SwallowUnsubscribe<T> implements Observable.Operator<T, T> {

        @Override
        public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
            return new Subscriber<T>() {
                @Override
                public void onCompleted() {
                    subscriber.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    subscriber.onError(e);
                }

                @Override
                public void onNext(T t) {
                    subscriber.onNext(t);
                }
            };
        }

    }
}
