package net.jqwik.micronaut.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import net.jqwik.micronaut.beans.math.MathService;

@Controller("/math")
public class MathController {
    private final MathService mathService;

    MathController(final MathService mathService) {
        this.mathService = mathService;
    }

    @Get(uri = "/compute/{number}", processes = MediaType.TEXT_PLAIN)
    String compute(final Integer number) {
        return String.valueOf(mathService.compute(number));
    }
}
