# RxMocks
[![Apache Licence 2.0](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

Mocks/Assertions for RxJava testing

## Description

RxMocks allows you to generate a mock of your repository providing Observables.
The observables returned by this mocked repo can then be injected with events to test components using RxJava.
You can consider it the equivalent of Mockito (when/thenReturn) in the Asynchronous world.

The second part of this is RxExpect and this allows you to write assertions against Observables to verify that specific events are sent through.

At the moment this will only mock methods from the interface returning observables (see Future improvements section).

This project is in its early stages, feel free to comment, and contribute back to help us improve it.

## Adding to your project

To integrate RxMocks into your project, add the following at the beginning of the `build.gradle` of your project:

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        testCompile 'com.novoda:rxmocks:0.1'
    }
}
```


## Simple usage

To generate a mocked repo use an interface providing Observables as an abstraction for your repo.
You can now use this interface to generate a mock as shown below.

**Interfaced repository**
```java
public interface DataRepository {

    Observable<User> getUser(String name);

    Observable<Articles> getArticles();

}
```

**Mocking this repository**
```java
DataRepository mockedRepo = RxMocks.mock(DataRepository.class)
```

**Inject data in provided Observables**
```java
Observable<User> user = mockedRepo.getUser("some name");

ClassToTest testedObject = new ClassToTest(user);

RxMocks.with(mockedRepo)
          .sendEventsFrom(Observable.just(new User("some name")))
          .to(user);
```

**Inject Errors**
```java
RxMocks.with(mockedRepo)
          .sendEventsFrom(Observable.error(new SomeCustomError()))
          .to(user);
```

**Reset mocks between tests**
```java
RxMocks.with(mockedRepo).resetMocks();
```

**Use RxExpect on actual observables to assert proper behaviour**
```java
Observable<User> user = Observable.just(new User("some name"));

RxExpect.expect(any(User.class), user);

Observable<User> error = Observable.error(new CustomError());

RxExpect.expect(anyError(User.class, CustomError.class), error);
```

**Use custom matchers for more fine assertions**
```java
Observable<User> user = Observable.just(new User("some name"));

RxExpect.expect(
    new RxMatcher<Notification<User>>() {
           @Override
           public boolean matches(Notification<User> actual) {
               return actual.getValue().name().equals("some name");
           }

           @Override
           public String description() {
               return "User with name " + "some name";
           }
    }, 
    user
);
```

## Future improvements

- Support "spying" to allow for non mocked calls to be forwarded to actual implementation.

## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/novoda/rxmocks/issues) first to see if we are working on it
 * Looking for community help, browse the already asked [Stack Overflow Questions](http://stackoverflow.com/questions/tagged/support-rxmocks) or use the tag: `support-rxmocks` when posting a new question
