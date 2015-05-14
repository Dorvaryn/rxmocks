package com.novoda.rxmocks.demo;

import rx.Observable;

public interface DataRepository {

    Observable<Integer> getRandomNumber(int max);

}
