package net.jqwik.micronaut.beans.named;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named("B")
class B implements MyInterface {
    @Override
    public String test() {
        return "B";
    }
}
