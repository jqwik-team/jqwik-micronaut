package net.jqwik.micronaut.beans;

import jakarta.inject.Singleton;

@Singleton
public class SimpleService {
    private final SimpleWorker simpleWorker;

    public SimpleService(final SimpleWorker simpleWorker) {
        this.simpleWorker = simpleWorker;
    }

    public SimpleWorker getSimpleWorker() {
        return simpleWorker;
    }
}
