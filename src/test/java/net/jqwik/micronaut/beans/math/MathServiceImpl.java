package net.jqwik.micronaut.beans.math;

import jakarta.inject.Singleton;

@Singleton
public
class MathServiceImpl implements MathService {
    @Override
    public Integer compute(final Integer num) {
        return num * 4;
    }
}
