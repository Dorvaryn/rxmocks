package com.novoda.rxmocks;

public interface RxMatcher<T> {

    boolean matches(T actual);

    String description();

}
