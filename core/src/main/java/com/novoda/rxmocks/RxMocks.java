package com.novoda.rxmocks;

import java.lang.reflect.Proxy;

public final class RxMocks {

    private RxMocks() {
    }

    /**
     * Creates a mock of the interface {@code T}
     * @param type The class of interface {@code T} to mock
     * @param <T> The interface to mock
     * @return A mock object implementing the interface {@code type}
     */
    public static <T> T mock(Class<T> type) {
        ClassLoader classLoader = type.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{type, RxMockerInvocationProxy.class}, new RxMocksInvocationHandler());
    }

    /**
     * Initiate an action on the mock {@code repo}
     * @param repo The mocked repo to work with
     * @return A mocker object to interact with the mock {@code repo}
     */
    public static RxMocker with(Object repo) {
        return new RxMocker(repo);
    }

}
