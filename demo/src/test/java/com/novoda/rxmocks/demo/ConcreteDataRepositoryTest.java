package com.novoda.rxmocks.demo;

import com.novoda.rxmocks.RxMatcher;

import org.junit.Test;

import rx.Notification;
import rx.Observable;

import static com.novoda.rxmocks.RxExpect.expect;

public class ConcreteDataRepositoryTest {

    @Test
    public void getRandomNumberReturnsAnIntLowerThanTheMaxPassedAsParam() throws Exception {
        DataRepository repository = new ConcreteDataRepository();

        Observable<Integer> randomNumber = repository.getRandomNumber(10);

        expect(lowerThan(10), randomNumber);
    }

    private RxMatcher<Notification<Integer>> lowerThan(final int bound) {
        return new RxMatcher<Notification<Integer>>() {
            @Override
            public boolean matches(Notification<Integer> actual) {
                return (actual.getKind() == Notification.Kind.OnNext) && (actual.getValue() < bound);
            }

            @Override
            public String description() {
                return "Integer lower than 10";
            }
        };
    }
}
