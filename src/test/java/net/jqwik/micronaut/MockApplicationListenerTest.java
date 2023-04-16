package net.jqwik.micronaut;

import io.micronaut.context.BeanContext;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest
class MockApplicationListenerTest {
    @Inject
    private BeanContext beanContext;

    @Property(tries = 1)
    public void test() {
        MyApplicationListener myApplicationListener = beanContext.getBean(MyApplicationListener.class);
        assertThat(myApplicationListener.getDescription()).isEqualTo("I'm the mock bean");
    }

    @MockBean(MyApplicationListener.class)
    public MyApplicationListener mockBean() {
        return new MyApplicationListener("I'm the mock bean");
    }
}

@Singleton
class MyApplicationListener implements ApplicationEventListener<StartupEvent> {
    private final String description;

    public MyApplicationListener(final String description) {
        this.description = description;
    }

    @Inject
    public MyApplicationListener() {
        this.description = "Real bean";
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        // no-op
    }
}