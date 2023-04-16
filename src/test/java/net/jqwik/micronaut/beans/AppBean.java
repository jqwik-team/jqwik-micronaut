package net.jqwik.micronaut.beans;

import jakarta.inject.Singleton;

@Singleton
public class AppBean {
    public String method() {
        return "Hello world!";
    }
}
