package com.novoda.rxmocks;

import java.lang.reflect.Array;

import org.junit.Before;
import org.junit.Test;

import rx.Notification;
import rx.Observable;
import rx.functions.Action1;

import static org.fest.assertions.api.Assertions.assertThat;

public class RxMocksTest {

    private TestRepository repository;

    @Before
    public void setUp() throws Exception {
        repository = RxMocks.mock(TestRepository.class);
    }

    @Test
    public void itSendsEventsToMockedObservable() throws Exception {
        Observable<Integer> foo = repository.foo(3);

        RxMocks.with(repository)
                .sendEventsFrom(SimpleEvents.onNext(42))
                .to(foo);

        Integer result = foo.toBlocking().first();

        assertThat(result).isEqualTo(42);
    }

    @Test
    public void itSendsEventsToMockedObservableAccordingToParameter() throws Exception {
        Observable<Integer> foo = repository.foo(3);
        Observable<Integer> bar = repository.foo(1);

        RxMocks.with(repository)
                .sendEventsFrom(SimpleEvents.onNext(42))
                .to(foo);
        RxMocks.with(repository)
                .sendEventsFrom(SimpleEvents.onNext(24))
                .to(bar);

        Integer result = foo.toBlocking().first();
        Integer result2 = bar.toBlocking().first();

        assertThat(result).isEqualTo(42);
        assertThat(result2).isEqualTo(24);
    }

    @Test
    public void itDeterminesWetherAnObservableIsProvidedByAGivenRepository() throws Exception {
        Observable<Integer> foo = repository.foo(3);

        boolean result = RxMocks.with(repository)
                .provides(foo);
        boolean result2 = RxMocks.with(repository)
                .provides(Observable.just(1));

        assertThat(result).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    public void itProvidesTheSameObservableForTheSameMethodParamCombination() throws Exception {
        Observable<Integer> foo = repository.foo(3);
        Observable<Integer> bar = repository.foo(3);

        assertThat(foo).isEqualTo(bar);
    }

    @Test
    public void resetMocksResetsPipelines() throws Exception {
        Observable<Integer> foo = repository.foo(3);

        RxMocks.with(repository)
                .sendEventsFrom(SimpleEvents.onNext(42))
                .to(foo);

        RxMocks.with(repository).resetMocks();

        Observable<Integer> bar = repository.foo(3);

        RxMocks.with(repository)
                .sendEventsFrom(SimpleEvents.<Integer>onCompleted())
                .to(bar);

        Boolean result = bar.isEmpty().toBlocking().first();

        assertThat(result).isTrue();
    }

    @Test
    public void getEventsForDoesNotAffectSubscriptionToMockeObservables() throws Exception {
        Observable<Integer> foo = repository.foo(3);

        final Notification<Integer>[] test = (Notification<Integer>[]) Array.newInstance(Notification.class, 1);
        RxMocks.with(repository)
                .getEventsFor(foo)
                .subscribe(
                        new Action1<Notification<Integer>>() {
                            @Override
                            public void call(Notification<Integer> integerNotification) {
                                test[0] = integerNotification;
                            }
                        });

        RxMocks.with(repository)
                .sendEventsFrom(SimpleEvents.onNext(42))
                .to(foo);

        assertThat(test[0]).isNull();

        Integer result = foo.toBlocking().first();

        assertThat(test[0].getKind()).isEqualTo(Notification.Kind.OnNext);
        assertThat(test[0].getValue()).isEqualTo(42);
    }

    public interface TestRepository {
        Observable<Integer> foo(int bar);
    }
}
