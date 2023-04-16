package net.jqwik.micronaut;

import io.micronaut.test.annotation.MockBean;
import net.jqwik.micronaut.beans.SimpleService;

import static org.mockito.Mockito.mock;

abstract class SimpleBaseTest {
    @MockBean(SimpleService.class)
    SimpleService simpleService() {
        return mock(SimpleService.class);
    }
}