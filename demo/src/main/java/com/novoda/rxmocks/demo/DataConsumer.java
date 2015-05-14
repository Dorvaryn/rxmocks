package com.novoda.rxmocks.demo;

import rx.Observer;

/**
 * A quick and dirty example of a class using a DataRepository that we might want to test.
 */
public class DataConsumer {

    private final DataRepository repository;

    private Integer result;
    private Throwable error;

    public DataConsumer(DataRepository repository) {
        this.repository = repository;
    }

    public void init() {
        repository.getRandomNumber(10)
                .subscribe(new IntegerObserver());
    }

    public Integer getResult() throws Throwable {
        if (error != null) {
            throw error;
        }
        return result;
    }

    private class IntegerObserver implements Observer<Integer> {
        @Override
        public void onCompleted() {
            //No op
        }

        @Override
        public void onError(Throwable e) {
            error = e;
        }

        @Override
        public void onNext(Integer integer) {
            result = integer;
        }
    }
}
