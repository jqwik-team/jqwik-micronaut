package net.jqwik.micronaut.beans.named;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named("A")
class A implements MyInterface {
    @Override
    public String test() {
        return "A";
    }
}
