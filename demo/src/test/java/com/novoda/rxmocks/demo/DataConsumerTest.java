package com.novoda.rxmocks.demo;

import com.novoda.rxmocks.RxMocks;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import rx.Observable;

import static org.fest.assertions.api.Assertions.assertThat;

public class DataConsumerTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private DataRepository mockedDataRepo;

    private DataConsumer consumer;

    @Before
    public void setUp() throws Exception {
        mockedDataRepo = RxMocks.mock(DataRepository.class);
        consumer = new DataConsumer(mockedDataRepo);
        consumer.init();
    }

    @Test
    public void consumesDataFromRepo() throws Throwable {
        RxMocks.with(mockedDataRepo)
                .sendEventsFrom(Observable.just(3))
                .to(mockedDataRepo.getRandomNumber(10));

        Integer result = consumer.getResult();

        assertThat(result).isEqualTo(3);
    }

    @Test
    public void throwsErrorIfRepoFetchFails() throws Throwable {
        expectedEx.expect(IOException.class);
        expectedEx.expectMessage("No connection");

        RxMocks.with(mockedDataRepo)
                .sendEventsFrom(Observable.<Integer>error(new IOException("No connection")))
                .to(mockedDataRepo.getRandomNumber(10));

        consumer.getResult();
    }
}
